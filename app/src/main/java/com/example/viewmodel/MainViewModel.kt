package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiTeacherRepository
import com.example.ai.AiTeacherUiState
import com.example.ai.SpeechInputLanguage
import com.example.ai.TeacherChatMessage
import com.example.ai.TeacherTopic
import com.example.data.VocabularyRepository
import com.example.model.AvailableAvatars
import com.example.model.KidAvatar
import com.example.model.KidBadge
import com.example.model.ScreenRoute
import com.example.model.SpeechEvaluationResult
import com.example.model.SpeechMatchStatus
import com.example.model.UserProfile
import com.example.model.WordCategory
import com.example.model.WordItem
import com.example.speech.SpeechManager
import com.example.speech.SpeechState
import com.example.update.AppUpdateManager
import com.example.update.UpdateUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LetterBubble(
    val id: Int,
    val char: Char,
    val isUsed: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val speechManager = SpeechManager(application.applicationContext)
    private val appUpdateManager = AppUpdateManager(application.applicationContext)
    private val aiTeacherRepository = AiTeacherRepository()

    // Auto-update state
    private val _updateState = MutableStateFlow<UpdateUiState>(UpdateUiState.Idle)
    val updateState: StateFlow<UpdateUiState> = _updateState.asStateFlow()

    val currentAppVersion: String
        get() = appUpdateManager.getCurrentVersionName()

    // AI Teacher Conversation State
    private val _teacherMessages = MutableStateFlow<List<TeacherChatMessage>>(emptyList())
    val teacherMessages: StateFlow<List<TeacherChatMessage>> = _teacherMessages.asStateFlow()

    private val _aiTeacherUiState = MutableStateFlow<AiTeacherUiState>(AiTeacherUiState.Idle)
    val aiTeacherUiState: StateFlow<AiTeacherUiState> = _aiTeacherUiState.asStateFlow()

    private val _isTeacherSpeaking = MutableStateFlow(false)
    val isTeacherSpeaking: StateFlow<Boolean> = _isTeacherSpeaking.asStateFlow()

    private val _teacherAutoSpeak = MutableStateFlow(true)
    val teacherAutoSpeak: StateFlow<Boolean> = _teacherAutoSpeak.asStateFlow()

    private val _teacherSlowVoice = MutableStateFlow(false)
    val teacherSlowVoice: StateFlow<Boolean> = _teacherSlowVoice.asStateFlow()

    private val _teacherInputLanguage = MutableStateFlow(SpeechInputLanguage.ENGLISH_IN)
    val teacherInputLanguage: StateFlow<SpeechInputLanguage> = _teacherInputLanguage.asStateFlow()

    private val _teacherSpeechState = MutableStateFlow(SpeechState.IDLE)
    val teacherSpeechState: StateFlow<SpeechState> = _teacherSpeechState.asStateFlow()

    private val _teacherLiveTranscript = MutableStateFlow("")
    val teacherLiveTranscript: StateFlow<String> = _teacherLiveTranscript.asStateFlow()

    private val _teacherMicError = MutableStateFlow<String?>(null)
    val teacherMicError: StateFlow<String?> = _teacherMicError.asStateFlow()

    private var lastChildPromptForRetry: String? = null
    private var hasGreetedInTeacherScreen: Boolean = false

    // Navigation
    private val _currentScreen = MutableStateFlow(ScreenRoute.HOME)
    val currentScreen: StateFlow<ScreenRoute> = _currentScreen.asStateFlow()

    // User & Badges
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Practice / Word Deck
    private val _selectedCategory = MutableStateFlow(WordCategory.ANIMALS)
    val selectedCategory: StateFlow<WordCategory> = _selectedCategory.asStateFlow()

    private val _categoryWords = MutableStateFlow(VocabularyRepository.getWordsByCategory(WordCategory.ANIMALS))
    val categoryWords: StateFlow<List<WordItem>> = _categoryWords.asStateFlow()

    private val _currentWordIndex = MutableStateFlow(0)
    val currentWordIndex: StateFlow<Int> = _currentWordIndex.asStateFlow()

    val currentPracticeWord: WordItem
        get() = _categoryWords.value.getOrElse(_currentWordIndex.value) { _categoryWords.value.first() }

    // Speech Evaluation
    private val _speechState = MutableStateFlow(SpeechState.IDLE)
    val speechState: StateFlow<SpeechState> = _speechState.asStateFlow()

    private val _rmsDb = MutableStateFlow(0f)
    val rmsDb: StateFlow<Float> = _rmsDb.asStateFlow()

    private val _lastSpeechResult = MutableStateFlow<SpeechEvaluationResult?>(null)
    val lastSpeechResult: StateFlow<SpeechEvaluationResult?> = _lastSpeechResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Confetti trigger
    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> = _showConfetti.asStateFlow()

    // Game 1: Picture Quiz
    private val _quizWord = MutableStateFlow(VocabularyRepository.getRandomWord())
    val quizWord: StateFlow<WordItem> = _quizWord.asStateFlow()

    private val _quizOptions = MutableStateFlow(VocabularyRepository.getQuizOptions(_quizWord.value))
    val quizOptions: StateFlow<List<WordItem>> = _quizOptions.asStateFlow()

    private val _selectedQuizOption = MutableStateFlow<WordItem?>(null)
    val selectedQuizOption: StateFlow<WordItem?> = _selectedQuizOption.asStateFlow()

    private val _isQuizAnswerCorrect = MutableStateFlow<Boolean?>(null)
    val isQuizAnswerCorrect: StateFlow<Boolean?> = _isQuizAnswerCorrect.asStateFlow()

    // Game 2: Listen & Find
    private val _listenWord = MutableStateFlow(VocabularyRepository.getRandomWord())
    val listenWord: StateFlow<WordItem> = _listenWord.asStateFlow()

    private val _listenOptions = MutableStateFlow(VocabularyRepository.getQuizOptions(_listenWord.value))
    val listenOptions: StateFlow<List<WordItem>> = _listenOptions.asStateFlow()

    private val _selectedListenOption = MutableStateFlow<WordItem?>(null)
    val selectedListenOption: StateFlow<WordItem?> = _selectedListenOption.asStateFlow()

    private val _isListenCorrect = MutableStateFlow<Boolean?>(null)
    val isListenCorrect: StateFlow<Boolean?> = _isListenCorrect.asStateFlow()

    // Game 3: Word Builder (Spelling Balloon)
    private val _builderWord = MutableStateFlow(VocabularyRepository.allWords.filter { it.word.length in 3..6 }.random())
    val builderWord: StateFlow<WordItem> = _builderWord.asStateFlow()

    private val _builderBubbles = MutableStateFlow<List<LetterBubble>>(emptyList())
    val builderBubbles: StateFlow<List<LetterBubble>> = _builderBubbles.asStateFlow()

    private val _spelledLetters = MutableStateFlow<List<LetterBubble>>(emptyList())
    val spelledLetters: StateFlow<List<LetterBubble>> = _spelledLetters.asStateFlow()

    private val _isWordBuilt = MutableStateFlow(false)
    val isWordBuilt: StateFlow<Boolean> = _isWordBuilt.asStateFlow()

    // Word Vault Search
    private val _vaultSearch = MutableStateFlow("")
    val vaultSearch: StateFlow<String> = _vaultSearch.asStateFlow()

    private val _vaultCategory = MutableStateFlow<WordCategory?>(null)
    val vaultCategory: StateFlow<WordCategory?> = _vaultCategory.asStateFlow()

    init {
        setupWordBuilder()
        seedTeacherWelcomeMessage()
        checkForAppUpdate(isManual = false)
    }

    private fun seedTeacherWelcomeMessage() {
        val kidName = _userProfile.value.kidName
        _teacherMessages.value = listOf(
            TeacherChatMessage(
                id = "welcome_${System.currentTimeMillis()}",
                isFromTeacher = true,
                englishText = "Hello! I am Meera Teacher. Let us learn English together! Press the microphone button and say Hello to me!",
                marathiText = "नमस्ते $kidName! मी मीरा टीचर आहे. आज आपण बोलून मजेत इंग्रजी शिकूया. खालील मायक्रोफोन दाबून मला 'Hello' म्हण किंवा मराठीत काहीही विचार!",
                practicePhrase = "Hello Meera Teacher!",
                quickReplies = listOf(
                    "Hello Meera Teacher!",
                    "Teach me an animal word",
                    "Tell me a short story"
                )
            )
        )
    }

    fun sendMessageToTeacher(rawText: String) {
        val message = rawText.trim()
        if (message.isEmpty() || _aiTeacherUiState.value is AiTeacherUiState.Thinking) return

        speechManager.stopListening()
        speechManager.stopSpeaking()
        _teacherSpeechState.value = SpeechState.IDLE
        _teacherLiveTranscript.value = ""
        _teacherMicError.value = null
        lastChildPromptForRetry = message

        val childMsg = TeacherChatMessage(
            id = "child_${System.currentTimeMillis()}",
            isFromTeacher = false,
            englishText = message
        )
        _teacherMessages.value = _teacherMessages.value + childMsg
        requestTeacherReply(message)
    }

    fun retryLastTeacherMessage() {
        val retryText = lastChildPromptForRetry ?: return
        if (_aiTeacherUiState.value is AiTeacherUiState.Thinking) return
        requestTeacherReply(retryText)
    }

    private fun requestTeacherReply(prompt: String) {
        viewModelScope.launch {
            _aiTeacherUiState.value = AiTeacherUiState.Thinking
            try {
                val reply = aiTeacherRepository.sendChildMessage(
                    userMessage = prompt,
                    kidName = _userProfile.value.kidName
                )
                val teacherMsg = TeacherChatMessage(
                    id = "teacher_${System.currentTimeMillis()}",
                    isFromTeacher = true,
                    englishText = reply.englishSpeech,
                    marathiText = reply.marathiSupport,
                    practicePhrase = reply.practicePhrase,
                    quickReplies = reply.quickReplies
                )
                _teacherMessages.value = _teacherMessages.value + teacherMsg
                _aiTeacherUiState.value = AiTeacherUiState.Ready
                addStars(1)

                if (_teacherAutoSpeak.value) {
                    replayTeacherMessage(teacherMsg, isSlow = _teacherSlowVoice.value)
                }
            } catch (e: TimeoutCancellationException) {
                Log.e("MainViewModel", "AI Teacher timed out", e)
                _aiTeacherUiState.value = AiTeacherUiState.Error(
                    "उत्तर मिळायला वेळ लागला (60s timeout). कृपया पुन्हा प्रयत्न करा."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("MainViewModel", "AI Teacher call failed", e)
                _aiTeacherUiState.value = AiTeacherUiState.Error(
                    e.localizedMessage ?: "AI टीचरशी संपर्क होऊ शकला नाही. कृपया पुन्हा प्रयत्न करा."
                )
            }
        }
    }

    fun selectTeacherTopic(topic: TeacherTopic) {
        sendMessageToTeacher(topic.starterPrompt)
    }

    fun toggleTeacherVoiceInput() {
        if (_teacherSpeechState.value == SpeechState.LISTENING) {
            speechManager.stopListening()
            _teacherSpeechState.value = SpeechState.IDLE
            _teacherLiveTranscript.value = ""
            return
        }

        _teacherMicError.value = null
        _teacherLiveTranscript.value = ""
        speechManager.startConversationalListening(
            languageTag = _teacherInputLanguage.value.localeTag,
            onRmsChanged = { rms -> _rmsDb.value = rms },
            onStateChange = { state -> _teacherSpeechState.value = state },
            onPartialText = { partial -> _teacherLiveTranscript.value = partial },
            onFinalText = { spoken ->
                _teacherLiveTranscript.value = ""
                sendMessageToTeacher(spoken)
            },
            onError = { err ->
                _teacherLiveTranscript.value = ""
                _teacherMicError.value = err
            }
        )
    }

    fun replayTeacherMessage(msg: TeacherChatMessage, isSlow: Boolean) {
        speechManager.speakTeacherResponse(
            englishText = msg.englishText,
            marathiText = msg.marathiText,
            isSlow = isSlow,
            speakMarathiAlso = true,
            onSpeakingChanged = { speaking -> _isTeacherSpeaking.value = speaking }
        )
    }

    fun stopTeacherSpeaking() {
        speechManager.stopSpeaking()
        _isTeacherSpeaking.value = false
    }

    fun toggleTeacherAutoSpeak() {
        val next = !_teacherAutoSpeak.value
        _teacherAutoSpeak.value = next
        if (!next) {
            stopTeacherSpeaking()
        }
    }

    fun toggleTeacherSlowVoice() {
        _teacherSlowVoice.value = !_teacherSlowVoice.value
    }

    fun setTeacherInputLanguage(language: SpeechInputLanguage) {
        _teacherInputLanguage.value = language
    }

    fun resetTeacherConversation() {
        speechManager.stopListening()
        stopTeacherSpeaking()
        _teacherSpeechState.value = SpeechState.IDLE
        _teacherLiveTranscript.value = ""
        _teacherMicError.value = null
        _aiTeacherUiState.value = AiTeacherUiState.Idle
        aiTeacherRepository.startNewSession(_userProfile.value.kidName)
        seedTeacherWelcomeMessage()
        val firstMsg = _teacherMessages.value.firstOrNull()
        if (firstMsg != null && _teacherAutoSpeak.value) {
            replayTeacherMessage(firstMsg, isSlow = _teacherSlowVoice.value)
        }
    }

    fun checkForAppUpdate(isManual: Boolean = true) {
        if (_updateState.value is UpdateUiState.Checking) return
        if (isManual) {
            _updateState.value = UpdateUiState.Checking
        }
        viewModelScope.launch {
            _updateState.value = appUpdateManager.checkForLatestRelease(isManual = isManual)
        }
    }

    fun showUpdateDialog() {
        val current = _updateState.value
        if (current is UpdateUiState.UpdateAvailable) {
            _updateState.value = current.copy(showDialog = true)
        }
    }

    fun dismissUpdateDialog(rememberDismissal: Boolean = true) {
        val current = _updateState.value
        if (current is UpdateUiState.UpdateAvailable) {
            if (rememberDismissal) {
                appUpdateManager.markTagDismissed(current.release.tagName)
            }
            _updateState.value = current.copy(showDialog = false)
        }
    }

    fun openUpdateDownloadUrl() {
        val current = _updateState.value
        if (current is UpdateUiState.UpdateAvailable) {
            appUpdateManager.openExternalUrl(current.release.downloadUrl)
        }
    }

    fun openReleasePageUrl() {
        val current = _updateState.value
        if (current is UpdateUiState.UpdateAvailable) {
            appUpdateManager.openExternalUrl(current.release.releasePageUrl)
        }
    }

    fun navigateTo(route: ScreenRoute) {
        _currentScreen.value = route
        _lastSpeechResult.value = null
        _errorMessage.value = null
        _teacherMicError.value = null
        _teacherLiveTranscript.value = ""
        _showConfetti.value = false
        speechManager.stopListening()
        _teacherSpeechState.value = SpeechState.IDLE
        if (route != ScreenRoute.AI_TEACHER) {
            stopTeacherSpeaking()
        }

        if (route == ScreenRoute.PICTURE_QUIZ && _selectedQuizOption.value != null) {
            nextQuizQuestion()
        } else if (route == ScreenRoute.LISTEN_FIND) {
            speakCurrentListenWord()
        } else if (route == ScreenRoute.AI_TEACHER && !hasGreetedInTeacherScreen) {
            hasGreetedInTeacherScreen = true
            val welcome = _teacherMessages.value.firstOrNull()
            if (welcome != null && _teacherAutoSpeak.value) {
                replayTeacherMessage(welcome, isSlow = _teacherSlowVoice.value)
            }
        }
    }

    fun selectCategory(category: WordCategory) {
        _selectedCategory.value = category
        _categoryWords.value = VocabularyRepository.getWordsByCategory(category)
        _currentWordIndex.value = 0
        _lastSpeechResult.value = null
        _errorMessage.value = null
        _speechState.value = SpeechState.IDLE
        speechManager.stopListening()
    }

    fun selectWord(word: WordItem) {
        _selectedCategory.value = word.category
        val list = VocabularyRepository.getWordsByCategory(word.category)
        _categoryWords.value = list
        val index = list.indexOfFirst { it.id == word.id }
        _currentWordIndex.value = if (index >= 0) index else 0
        _lastSpeechResult.value = null
        _errorMessage.value = null
        _speechState.value = SpeechState.IDLE
    }

    fun nextPracticeWord() {
        if (_categoryWords.value.isEmpty()) return
        val nextIdx = (_currentWordIndex.value + 1) % _categoryWords.value.size
        _currentWordIndex.value = nextIdx
        _lastSpeechResult.value = null
        _errorMessage.value = null
        _showConfetti.value = false
        _speechState.value = SpeechState.IDLE
        speechManager.stopListening()
        // Auto pronounce the new word gently for kids
        speakWord(currentPracticeWord.word)
    }

    fun previousPracticeWord() {
        if (_categoryWords.value.isEmpty()) return
        val prevIdx = if (_currentWordIndex.value - 1 < 0) _categoryWords.value.size - 1 else _currentWordIndex.value - 1
        _currentWordIndex.value = prevIdx
        _lastSpeechResult.value = null
        _errorMessage.value = null
        _showConfetti.value = false
        _speechState.value = SpeechState.IDLE
        speechManager.stopListening()
        speakWord(currentPracticeWord.word)
    }

    fun speakCurrentWord(isSlow: Boolean = false) {
        speechManager.speak(currentPracticeWord.word, isSlow)
    }

    fun speakWord(word: String, isSlow: Boolean = false) {
        speechManager.speak(word, isSlow)
    }

    fun toggleSpeechRecognition() {
        if (_speechState.value == SpeechState.LISTENING) {
            speechManager.stopListening()
            _speechState.value = SpeechState.IDLE
        } else {
            startListening()
        }
    }

    fun startListening() {
        _errorMessage.value = null
        _lastSpeechResult.value = null
        _showConfetti.value = false

        speechManager.startListening(
            targetWord = currentPracticeWord.word,
            onRmsChanged = { rms -> _rmsDb.value = rms },
            onStateChange = { state -> _speechState.value = state },
            onResult = { result ->
                _lastSpeechResult.value = result
                if (result.isSuccess) {
                    addStars(result.starsEarned)
                    _showConfetti.value = true
                    recordPracticeWord()
                }
            },
            onError = { err ->
                _errorMessage.value = err
            }
        )
    }

    private fun addStars(count: Int) {
        if (count <= 0) return
        val current = _userProfile.value
        val newStars = current.totalStars + count
        val newPracticeCount = current.speechPracticeCount + 1
        val newBadges = current.unlockedBadgeIds.toMutableSet()

        if (newStars >= 20) newBadges.add("star_collector")
        if (newStars >= 50) newBadges.add("speech_hero")
        if (newPracticeCount >= 10) newBadges.add("great_speaker")

        _userProfile.value = current.copy(
            totalStars = newStars,
            speechPracticeCount = newPracticeCount,
            unlockedBadgeIds = newBadges
        )
    }

    private fun recordPracticeWord() {
        val current = _userProfile.value
        _userProfile.value = current.copy(
            wordsPracticedCount = current.wordsPracticedCount + 1
        )
    }

    // GAME 1: PICTURE QUIZ
    fun answerQuiz(selected: WordItem) {
        if (_selectedQuizOption.value != null) return
        _selectedQuizOption.value = selected
        val correct = selected.id == _quizWord.value.id
        _isQuizAnswerCorrect.value = correct
        if (correct) {
            addStars(2)
            _showConfetti.value = true
            speakWord(selected.word)
            val current = _userProfile.value
            val newBadges = current.unlockedBadgeIds.toMutableSet().apply {
                add("quiz_champ")
            }
            _userProfile.value = current.copy(
                quizzesWonCount = current.quizzesWonCount + 1,
                unlockedBadgeIds = newBadges
            )
        } else {
            speakWord("Try again!")
        }
    }

    fun nextQuizQuestion() {
        val nextWord = VocabularyRepository.allWords.filter { it.id != _quizWord.value.id }.random()
        _quizWord.value = nextWord
        _quizOptions.value = VocabularyRepository.getQuizOptions(nextWord)
        _selectedQuizOption.value = null
        _isQuizAnswerCorrect.value = null
        _showConfetti.value = false
    }

    // GAME 2: LISTEN & FIND
    fun speakCurrentListenWord(isSlow: Boolean = false) {
        speechManager.speak(_listenWord.value.word, isSlow)
    }

    fun answerListenQuiz(selected: WordItem) {
        if (_selectedListenOption.value != null) return
        _selectedListenOption.value = selected
        val correct = selected.id == _listenWord.value.id
        _isListenCorrect.value = correct
        if (correct) {
            addStars(2)
            _showConfetti.value = true
            speakWord("Great job! " + selected.word)
        } else {
            speakWord("Oops! It was " + _listenWord.value.word)
        }
    }

    fun nextListenQuestion() {
        val nextWord = VocabularyRepository.allWords.filter { it.id != _listenWord.value.id }.random()
        _listenWord.value = nextWord
        _listenOptions.value = VocabularyRepository.getQuizOptions(nextWord)
        _selectedListenOption.value = null
        _isListenCorrect.value = null
        _showConfetti.value = false
        // Play audio automatically
        speakCurrentListenWord()
    }

    // GAME 3: WORD BUILDER
    private fun setupWordBuilder() {
        val word = _builderWord.value.word.uppercase()
        val bubbles = word.toCharArray().mapIndexed { index, char ->
            LetterBubble(id = index, char = char, isUsed = false)
        }.shuffled()
        _builderBubbles.value = bubbles
        _spelledLetters.value = emptyList()
        _isWordBuilt.value = false
    }

    fun tapBuilderLetter(bubble: LetterBubble) {
        if (bubble.isUsed || _isWordBuilt.value) return

        // Mark as used and append
        _builderBubbles.value = _builderBubbles.value.map {
            if (it.id == bubble.id) it.copy(isUsed = true) else it
        }
        val newSpelled = _spelledLetters.value + bubble
        _spelledLetters.value = newSpelled

        // Speak the single letter sound
        speechManager.speak(bubble.char.toString())

        // Check if full word formed
        val target = _builderWord.value.word.uppercase()
        val current = newSpelled.map { it.char }.joinToString("")
        if (current.length == target.length) {
            if (current == target) {
                _isWordBuilt.value = true
                addStars(3)
                _showConfetti.value = true
                speakWord("Fantastic! " + _builderWord.value.word)
                val prof = _userProfile.value
                val newBadges = prof.unlockedBadgeIds.toMutableSet().apply {
                    add("builder_star")
                }
                _userProfile.value = prof.copy(
                    wordsBuiltCount = prof.wordsBuiltCount + 1,
                    unlockedBadgeIds = newBadges
                )
            } else {
                // Incorrect order, reset after brief moment
                speakWord("Try again!")
            }
        }
    }

    fun undoLastBuilderLetter() {
        if (_spelledLetters.value.isEmpty() || _isWordBuilt.value) return
        val last = _spelledLetters.value.last()
        _spelledLetters.value = _spelledLetters.value.dropLast(1)
        _builderBubbles.value = _builderBubbles.value.map {
            if (it.id == last.id) it.copy(isUsed = false) else it
        }
    }

    fun resetBuilderWord() {
        setupWordBuilder()
    }

    fun nextBuilderWord() {
        val next = VocabularyRepository.allWords
            .filter { it.word.length in 3..6 && it.id != _builderWord.value.id }
            .random()
        _builderWord.value = next
        setupWordBuilder()
        _showConfetti.value = false
    }

    // VAULT SEARCH & FILTERS
    fun setVaultSearch(query: String) {
        _vaultSearch.value = query
    }

    fun setVaultCategory(category: WordCategory?) {
        _vaultCategory.value = category
    }

    // PROFILE
    fun selectAvatar(avatar: KidAvatar) {
        _userProfile.value = _userProfile.value.copy(selectedAvatar = avatar)
    }

    fun updateKidName(name: String) {
        if (name.isNotBlank()) {
            _userProfile.value = _userProfile.value.copy(kidName = name.trim())
        }
    }

    fun getAllBadges(): List<KidBadge> {
        val userBadges = _userProfile.value.unlockedBadgeIds
        return listOf(
            KidBadge("first_word", "पहिले पाऊल", "First Steps", "पहिला इंग्रजी शब्द उच्चारला!", "🌱", userBadges.contains("first_word")),
            KidBadge("star_collector", "तारे गोळा करणारा", "Star Collector", "२० तारे मिळवले!", "⭐", userBadges.contains("star_collector")),
            KidBadge("quiz_champ", "क्विझ चॅम्पियन", "Quiz Champion", "चित्र क्विझ योग्य सोडवली!", "🏆", userBadges.contains("quiz_champ")),
            KidBadge("great_speaker", "सुंदर वक्ता", "Great Speaker", "१० शब्दांचा उच्चार सराव केला!", "🎙️", userBadges.contains("great_speaker")),
            KidBadge("builder_star", "शब्द कारागीर", "Word Builder", "अक्षरे जुळवून शब्द बनवला!", "🧩", userBadges.contains("builder_star")),
            KidBadge("speech_hero", "इंग्लिश सुपरस्टार", "English Superstar", "५० तारे कमवून सुपरस्टार झालात!", "👑", userBadges.contains("speech_hero"))
        )
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.destroy()
    }
}
