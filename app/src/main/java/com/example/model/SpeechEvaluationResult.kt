package com.example.model

enum class SpeechMatchStatus {
    EXACT,       // 100% match
    VERY_CLOSE,  // 80-99% match
    CLOSE,       // 50-79% match
    TRY_AGAIN    // < 50% match
}

data class SpeechEvaluationResult(
    val targetWord: String,
    val recognizedWord: String,
    val accuracyPercentage: Int,
    val starsEarned: Int,
    val status: SpeechMatchStatus,
    val feedbackTitleMarathi: String,
    val feedbackTitleEnglish: String,
    val feedbackDescription: String,
    val isSuccess: Boolean
) {
    companion object {
        fun createFallback(target: String, userSpoken: String): SpeechEvaluationResult {
            return SpeechEvaluationResult(
                targetWord = target,
                recognizedWord = userSpoken,
                accuracyPercentage = 0,
                starsEarned = 0,
                status = SpeechMatchStatus.TRY_AGAIN,
                feedbackTitleMarathi = "पुन्हा प्रयत्न करा!",
                feedbackTitleEnglish = "Try Again!",
                feedbackDescription = "स्पष्ट आणि मोठ्याने बोला. तुम्ही नक्की करू शकता!",
                isSuccess = false
            )
        }
    }
}
