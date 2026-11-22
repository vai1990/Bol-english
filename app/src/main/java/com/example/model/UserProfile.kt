package com.example.model

data class KidAvatar(
    val id: String,
    val name: String,
    val marathiName: String,
    val emoji: String,
    val roleTitle: String
)

val AvailableAvatars = listOf(
    KidAvatar("parrot", "Mithu", "मिठू पोपट", "🦜", "बोलका मित्र"),
    KidAvatar("lion", "Simba", "सिंभा", "🦁", "धाडसी वाचक"),
    KidAvatar("bunny", "Bunny", "बन्नी ससा", "🐰", "चपळ खेळाडू"),
    KidAvatar("robot", "Robo", "रोबो", "🤖", "हुशार विद्यार्थी"),
    KidAvatar("kitty", "Mani", "मणी मांजर", "🐱", "गोड बोलणारी"),
    KidAvatar("bear", "Golu", "गोलू अस्वल", "🐻", "अभ्यासू मित्र")
)

data class KidBadge(
    val id: String,
    val titleMarathi: String,
    val titleEnglish: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean = false,
    val requiredStars: Int = 0
)

data class UserProfile(
    val kidName: String = "छोटे मित्र",
    val selectedAvatar: KidAvatar = AvailableAvatars[0],
    val totalStars: Int = 12,
    val streakDays: Int = 3,
    val wordsPracticedCount: Int = 6,
    val speechPracticeCount: Int = 4,
    val quizzesWonCount: Int = 2,
    val wordsBuiltCount: Int = 3,
    val unlockedBadgeIds: Set<String> = setOf("first_word", "first_quiz")
)
