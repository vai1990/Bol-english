package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BerryPink
import com.example.ui.theme.KiwiGreen
import com.example.ui.theme.OrangeFun
import com.example.ui.theme.PurpleFun
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SunnyYellow

enum class WordCategory(
    val englishName: String,
    val marathiName: String,
    val iconEmoji: String,
    val themeColor: Color
) {
    ANIMALS("Animals", "प्राणी", "🦁", KiwiGreen),
    FRUITS_VEGGIES("Fruits & Food", "फळे आणि भाज्या", "🍎", OrangeFun),
    COLORS_SHAPES("Colors & Shapes", "रंग आणि आकार", "🎨", BerryPink),
    VEHICLES("Vehicles", "वाहने", "🚗", SkyBluePrimary),
    EVERYDAY_SCHOOL("School & Home", "शाळा आणि घर", "📚", PurpleFun),
    ACTIONS("Action Words", "कृती आणि हालचाली", "🏃", SunnyYellow)
}

enum class DifficultyLevel {
    EASY,
    MEDIUM,
    CHALLENGE
}

data class WordItem(
    val id: String,
    val word: String,
    val phonetic: String,
    val marathiMeaning: String,
    val marathiPronounce: String,
    val category: WordCategory,
    val emoji: String,
    val exampleSentence: String,
    val marathiSentence: String,
    val funFact: String,
    val difficulty: DifficultyLevel = DifficultyLevel.EASY
)
