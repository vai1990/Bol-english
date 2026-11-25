package com.example

import com.example.data.VocabularyRepository
import com.example.model.WordCategory
import com.example.update.AppUpdateManager
import com.example.update.ReleaseInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

    @Test
    fun `semantic version comparison works across major minor patch`() {
        assertTrue(AppUpdateManager.compareSemanticVersions("1.2", "1.1") > 0)
        assertTrue(AppUpdateManager.compareSemanticVersions("2.0.0", "1.9.9") > 0)
        assertEquals(0, AppUpdateManager.compareSemanticVersions("1.1.0", "1.1"))
        assertTrue(AppUpdateManager.compareSemanticVersions("1.0", "1.1") < 0)
    }

    @Test
    fun `update detection matches sha256 semantic version and release timestamp`() {
        val sampleSha = "d62db275ffb504ca5b71bcac5f838f261e53d6b91154963f9594a62627101e2e"
        val newerRelease = ReleaseInfo(
            tagName = "debug-apk-build-5-1",
            title = "Debug APK Build 5",
            releaseNotes = "- **SHA-256:** `$sampleSha`",
            publishedAtIso = "2026-11-25T10:00:00Z",
            publishedAtMillis = 2_000_000_000_000L,
            downloadUrl = AppUpdateManager.FALLBACK_DOWNLOAD_URL,
            releasePageUrl = AppUpdateManager.FALLBACK_RELEASES_PAGE_URL,
            assetSizeBytes = 23_000_000L,
            remoteSha256 = sampleSha,
            remoteVersionName = null,
            remoteVersionCode = null
        )

        // Same APK SHA-256 already installed -> Up to date
        assertFalse(
            AppUpdateManager.isNewerRelease(
                release = newerRelease,
                currentVersionName = "1.1",
                currentVersionCode = 2,
                installedApkSha256 = sampleSha,
                installedUpdateTimeMs = 1_000_000_000_000L
            )
        )

        // Different SHA-256 and newer published_at -> Update available
        assertTrue(
            AppUpdateManager.isNewerRelease(
                release = newerRelease,
                currentVersionName = "1.1",
                currentVersionCode = 2,
                installedApkSha256 = "0000000000000000000000000000000000000000000000000000000000000000",
                installedUpdateTimeMs = 1_000_000_000_000L
            )
        )

        // Semantic version bump -> Update available
        val semverRelease = newerRelease.copy(tagName = "v1.2.0", remoteVersionName = "1.2.0", remoteSha256 = null)
        assertTrue(
            AppUpdateManager.isNewerRelease(
                release = semverRelease,
                currentVersionName = "1.1",
                currentVersionCode = 2,
                installedApkSha256 = null,
                installedUpdateTimeMs = 3_000_000_000_000L
            )
        )
    }

    @Test
    fun `trusted GitHub url validation rejects external domains`() {
        assertTrue(AppUpdateManager.isTrustedGitHubUrl("https://github.com/vai1990/Bol-english/releases/latest/download/app-debug.apk"))
        assertFalse(AppUpdateManager.isTrustedGitHubUrl("https://evil.example.com/app-debug.apk"))
    }

    @Test
    fun `ai teacher topics and bilingual input languages are configured`() {
        assertTrue(com.example.ai.TeacherTopic.values().size >= 5)
        com.example.ai.TeacherTopic.values().forEach { topic ->
            assertTrue(topic.marathiTitle.isNotBlank())
            assertTrue(topic.starterPrompt.isNotBlank())
        }
        val locales = com.example.ai.SpeechInputLanguage.values().map { it.localeTag }
        assertTrue(locales.contains("en-IN"))
        assertTrue(locales.contains("mr-IN"))
    }
}
