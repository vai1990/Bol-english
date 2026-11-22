package com.example.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.model.SpeechEvaluationResult
import com.example.model.SpeechMatchStatus
import java.util.Locale
import kotlin.math.max

enum class SpeechState {
    IDLE,
    LISTENING,
    PROCESSING,
    SUCCESS,
    ERROR
}

class SpeechManager(private val context: Context) : RecognitionListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    private var currentTargetWord: String = ""
    private var onRmsUpdate: ((Float) -> Unit)? = null
    private var onResultCallback: ((SpeechEvaluationResult) -> Unit)? = null
    private var onStateChangeCallback: ((SpeechState) -> Unit)? = null
    private var onErrorCallback: ((String) -> Unit)? = null

    init {
        initTts()
    }

    private fun initTts() {
        textToSpeech = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech?.setLanguage(Locale.getDefault())
                }
                textToSpeech?.setPitch(1.15f) // Slightly higher friendly pitch for kids
                isTtsReady = true
            } else {
                Log.e("SpeechManager", "TTS initialization failed")
            }
        }
    }

    fun speak(text: String, isSlow: Boolean = false) {
        if (!isTtsReady || textToSpeech == null) return
        val speechRate = if (isSlow) 0.65f else 0.88f // Calm & clear speed for kids
        textToSpeech?.setSpeechRate(speechRate)
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "KIDS_TTS_${System.currentTimeMillis()}")
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
    }

    fun isSpeechRecognitionAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun startListening(
        targetWord: String,
        onRmsChanged: (Float) -> Unit,
        onStateChange: (SpeechState) -> Unit,
        onResult: (SpeechEvaluationResult) -> Unit,
        onError: (String) -> Unit
    ) {
        currentTargetWord = targetWord
        this.onRmsUpdate = onRmsChanged
        this.onStateChangeCallback = onStateChange
        this.onResultCallback = onResult
        this.onErrorCallback = onError

        stopListening()

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(this@SpeechManager)
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US")
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }

            onStateChangeCallback?.invoke(SpeechState.LISTENING)
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e("SpeechManager", "Error starting speech recognition", e)
            onStateChangeCallback?.invoke(SpeechState.ERROR)
            onErrorCallback?.invoke("मायक्रोफोन सुरू होऊ शकला नाही. कृपया पुन्हा प्रयत्न करा.")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.e("SpeechManager", "Error stopping speech recognition", e)
        } finally {
            speechRecognizer = null
        }
    }

    fun destroy() {
        stopListening()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
    }

    // RecognitionListener Implementation
    override fun onReadyForSpeech(params: Bundle?) {
        onStateChangeCallback?.invoke(SpeechState.LISTENING)
    }

    override fun onBeginningOfSpeech() {
        onStateChangeCallback?.invoke(SpeechState.LISTENING)
    }

    override fun onRmsChanged(rmsdB: Float) {
        onRmsUpdate?.invoke(rmsdB)
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        onStateChangeCallback?.invoke(SpeechState.PROCESSING)
    }

    override fun onError(error: Int) {
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "ऑडिओ रेकॉर्डिंग समस्या."
            SpeechRecognizer.ERROR_CLIENT -> "कृपया पुन्हा बोला."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "मायक्रोफोन परवानगी द्या."
            SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "इंटरनेट कनेक्शन तपासा."
            SpeechRecognizer.ERROR_NO_MATCH -> "काही ऐकू आले नाही, पुन्हा स्पष्ट बोला!"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "मायक्रोफोन व्यस्त आहे."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "वेळ संपली! पुन्हा प्रयत्न करा."
            else -> "उच्चार ऐकू आला नाही. पुन्हा बोला!"
        }
        onStateChangeCallback?.invoke(SpeechState.ERROR)
        onErrorCallback?.invoke(errorMessage)
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: arrayListOf()
        val evaluation = evaluateChildPronunciation(currentTargetWord, matches)
        onStateChangeCallback?.invoke(if (evaluation.isSuccess) SpeechState.SUCCESS else SpeechState.IDLE)
        onResultCallback?.invoke(evaluation)
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val partialMatches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!partialMatches.isNullOrEmpty()) {
            val candidate = partialMatches[0].trim()
            if (candidate.equals(currentTargetWord, ignoreCase = true)) {
                // Early exact match!
                stopListening()
                val evaluation = evaluateChildPronunciation(currentTargetWord, listOf(candidate))
                onStateChangeCallback?.invoke(SpeechState.SUCCESS)
                onResultCallback?.invoke(evaluation)
            }
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}

    /**
     * Kid-friendly pronunciation evaluator:
     * Takes candidate speech strings and matches them with phonetic tolerance.
     */
    fun evaluateChildPronunciation(target: String, candidates: List<String>): SpeechEvaluationResult {
        if (candidates.isEmpty()) {
            return SpeechEvaluationResult.createFallback(target, "")
        }

        val cleanTarget = normalizePhonetic(target)
        var bestAccuracy = 0
        var bestRecognized = candidates.first()

        for (candidate in candidates) {
            val cleanCandidate = normalizePhonetic(candidate)
            val score = calculateMatchScore(cleanTarget, cleanCandidate)
            if (score > bestAccuracy) {
                bestAccuracy = score
                bestRecognized = candidate
            }
        }

        val stars: Int
        val status: SpeechMatchStatus
        val titleMr: String
        val titleEn: String
        val description: String
        val isSuccess: Boolean

        when {
            bestAccuracy >= 88 -> {
                stars = 3
                status = SpeechMatchStatus.EXACT
                titleMr = "🌟 अप्रतिम उच्चार! (Superstar!)"
                titleEn = "Awesome Pronunciation!"
                description = "तुम्ही अगदी हुबेहूब आणि स्पष्ट बोललात! ३ तारे मिळाले!"
                isSuccess = true
            }
            bestAccuracy >= 70 -> {
                stars = 2
                status = SpeechMatchStatus.VERY_CLOSE
                titleMr = "🎉 खूप छान प्रयत्न! (Great Job!)"
                titleEn = "Great Effort!"
                description = "फार सुंदर! थोडासा अजून सराव केल्यास परिपूर्ण होईल!"
                isSuccess = true
            }
            bestAccuracy >= 45 -> {
                stars = 1
                status = SpeechMatchStatus.CLOSE
                titleMr = "👍 छान सुरुवात! (Good Try!)"
                titleEn = "Good Start!"
                description = "छान! कासवाच्या गतीने ऐका आणि पुन्हा एकदा म्हणून बघा!"
                isSuccess = true
            }
            else -> {
                stars = 0
                status = SpeechMatchStatus.TRY_AGAIN
                titleMr = "💪 पुन्हा एकदा प्रयत्न करा!"
                titleEn = "Keep Trying!"
                description = "प्रथम 'ऐका' बटण दाबून ऐका, नंतर मायक्रोफोन दाबून बोला!"
                isSuccess = false
            }
        }

        return SpeechEvaluationResult(
            targetWord = target,
            recognizedWord = bestRecognized,
            accuracyPercentage = bestAccuracy,
            starsEarned = stars,
            status = status,
            feedbackTitleMarathi = titleMr,
            feedbackTitleEnglish = titleEn,
            feedbackDescription = description,
            isSuccess = isSuccess
        )
    }

    private fun normalizePhonetic(str: String): String {
        return str.lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9\\s]"), "")
            .trim()
            .replace("ph", "f") // Elephant / Elefant
            .replace("ck", "k") // Duck / Duk
    }

    private fun calculateMatchScore(target: String, spoken: String): Int {
        // Direct equal or spoken contains target (e.g. "it's a lion" -> target "lion")
        if (spoken == target) return 100
        val tokens = spoken.split("\\s+".toRegex())
        if (tokens.contains(target)) return 95

        for (token in tokens) {
            val tokenDist = levenshteinDistance(target, token)
            val maxLen = max(target.length, token.length)
            if (maxLen > 0) {
                val tokenScore = ((1.0 - (tokenDist.toDouble() / maxLen)) * 100).toInt()
                if (tokenScore >= 80) return tokenScore
            }
        }

        val dist = levenshteinDistance(target, spoken)
        val maxLen = max(target.length, spoken.length)
        if (maxLen == 0) return 0

        val score = ((1.0 - (dist.toDouble() / maxLen)) * 100).toInt()
        return score.coerceIn(0, 100)
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1,      // insertion
                    dp[i - 1][j - 1] + cost // substitution
                )
            }
        }
        return dp[s1.length][s2.length]
    }
}
