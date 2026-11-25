package com.example.ai

enum class TeacherTopic(
    val marathiTitle: String,
    val englishTitle: String,
    val emoji: String,
    val starterPrompt: String
) {
    INTRO(
        marathiTitle = "ओळख करूया",
        englishTitle = "Say Hello",
        emoji = "👋",
        starterPrompt = "Hello Meera Teacher! Let's start with a friendly greeting and teach me how to introduce myself in English."
    ),
    ANIMALS(
        marathiTitle = "प्राणी शिकूया",
        englishTitle = "Animals",
        emoji = "🦁",
        starterPrompt = "Meera Teacher, teach me a fun English sentence about an animal and ask me to repeat it!"
    ),
    FRUITS(
        marathiTitle = "फळे व खाऊ",
        englishTitle = "Fruits & Food",
        emoji = "🍎",
        starterPrompt = "Meera Teacher, let's talk about my favorite fruits in simple English!"
    ),
    STORY_TIME(
        marathiTitle = "छोटी गोष्ट",
        englishTitle = "Mini Story",
        emoji = "📖",
        starterPrompt = "Meera Teacher, tell me the first two short lines of a cute English story for kids and ask me what happens next!"
    ),
    RIDDLE_GAME(
        marathiTitle = "मजेदार कोडे",
        englishTitle = "Word Riddle",
        emoji = "🧩",
        starterPrompt = "Meera Teacher, ask me an easy fun English riddle so I can guess the English word!"
    ),
    SCHOOL_TALK(
        marathiTitle = "शाळेतील गप्पा",
        englishTitle = "School Talk",
        emoji = "🏫",
        starterPrompt = "Meera Teacher, let's practice simple English sentences we use at school!"
    )
}

enum class SpeechInputLanguage(
    val localeTag: String,
    val labelMarathi: String,
    val emoji: String
) {
    ENGLISH_IN("en-IN", "English बोला", "🇬🇧"),
    MARATHI_IN("mr-IN", "मराठीत विचारा", "🇮🇳")
}

data class TeacherChatMessage(
    val id: String,
    val isFromTeacher: Boolean,
    val englishText: String,
    val marathiText: String = "",
    val practicePhrase: String? = null,
    val quickReplies: List<String> = emptyList()
)

data class TeacherReplyPayload(
    val englishSpeech: String,
    val marathiSupport: String,
    val practicePhrase: String?,
    val quickReplies: List<String>
)

sealed interface AiTeacherUiState {
    data object Idle : AiTeacherUiState
    data object Thinking : AiTeacherUiState
    data object Ready : AiTeacherUiState
    data class Error(val message: String) : AiTeacherUiState
}
