package com.example

import com.example.data.VocabularyRepository
import com.example.model.SpeechMatchStatus
import com.example.model.WordCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `vocabulary repository contains required kid words and categories`() {
        val allWords = VocabularyRepository.allWords
        assertTrue("Repository has ample words", allWords.size >= 25)

        WordCategory.values().forEach { category ->
            val words = VocabularyRepository.getWordsByCategory(category)
            assertTrue("Category ${category.name} has items", words.isNotEmpty())
        }
    }

    @Test
    fun `random word and quiz options generation works correctly`() {
        val word = VocabularyRepository.getRandomWord()
        val options = VocabularyRepository.getQuizOptions(word, 4)
        assertEquals(4, options.size)
        assertTrue("Quiz options must contain the target word", options.any { it.id == word.id })
    }
}
