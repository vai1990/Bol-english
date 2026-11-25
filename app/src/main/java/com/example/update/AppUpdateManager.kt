package com.example.update

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class AppUpdateManager(
    private val context: Context,
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
) {
    companion object {
        const val GITHUB_OWNER = "vai1990"
        const val GITHUB_REPO = "Bol-english"
        const val LATEST_RELEASE_API_URL =
            "https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPO/releases/latest"
        const val FALLBACK_DOWNLOAD_URL =
            "https://github.com/$GITHUB_OWNER/$GITHUB_REPO/releases/latest/download/app-debug.apk"
        const val FALLBACK_RELEASES_PAGE_URL =
            "https://github.com/$GITHUB_OWNER/$GITHUB_REPO/releases/latest"

        private const val PREFS_NAME = "app_update_prefs"
        private const val KEY_DISMISSED_TAG = "dismissed_release_tag"
        private const val TAG = "AppUpdateManager"

        // Grace window (2 minutes) to avoid timestamp jitter right after installation
        private const val TIMESTAMP_GRACE_MS = 2 * 60 * 1000L

        internal fun parseIsoTimestamp(iso: String): Long {
            if (iso.isBlank()) return 0L
            val patterns = listOf(
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            )
            for (pattern in patterns) {
                try {
                    val sdf = SimpleDateFormat(pattern, Locale.US).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }
                    val parsed = sdf.parse(iso)
                    if (parsed != null) return parsed.time
                } catch (_: Exception) {
                    // Try next pattern
                }
            }
            return 0L
        }

        internal fun extractSemanticVersion(text: String): String? {
            if (text.isBlank()) return null
            val match = Regex("""(?i)(?:^|\b)v?(\d+\.\d+(?:\.\d+)?)(?:\b|$)""").find(text)
            return match?.groupValues?.getOrNull(1)
        }

        internal fun extractVersionCode(body: String): Int? {
            if (body.isBlank()) return null
            val match = Regex("""(?i)version\s*code\s*[:=]\s*(\d+)""").find(body)
            return match?.groupValues?.getOrNull(1)?.toIntOrNull()
        }

        internal fun extractSha256(body: String): String? {
            if (body.isBlank()) return null
            val match = Regex("""(?i)sha-?256\s*[:*`\s]+([a-f0-9]{64})""").find(body)
            return match?.groupValues?.getOrNull(1)?.lowercase(Locale.ROOT)
        }

        internal fun compareSemanticVersions(remote: String, current: String): Int {
            val remoteParts = remote.split(".").mapNotNull { it.toIntOrNull() }
            val currentParts = current.split(".").mapNotNull { it.toIntOrNull() }
            val maxLen = maxOf(remoteParts.size, currentParts.size)
            for (i in 0 until maxLen) {
                val r = remoteParts.getOrElse(i) { 0 }
                val c = currentParts.getOrElse(i) { 0 }
                if (r != c) return r.compareTo(c)
            }
            return 0
        }

        internal fun isTrustedGitHubUrl(url: String): Boolean {
            return url.startsWith("https://github.com/$GITHUB_OWNER/$GITHUB_REPO/") ||
                url.startsWith("https://objects.githubusercontent.com/")
        }

        internal fun isNewerRelease(
            release: ReleaseInfo,
            currentVersionName: String,
            currentVersionCode: Int,
            installedApkSha256: String?,
            installedUpdateTimeMs: Long
        ): Boolean {
            // If the exact APK binary from this release is already installed, never prompt
            if (!release.remoteSha256.isNullOrBlank() &&
                !installedApkSha256.isNullOrBlank() &&
                release.remoteSha256.equals(installedApkSha256, ignoreCase = true)
            ) {
                return false
            }

            // 1. Check explicit version code if provided in release notes
            if (release.remoteVersionCode != null) {
                if (release.remoteVersionCode > currentVersionCode) return true
                if (release.remoteVersionCode < currentVersionCode) return false
            }

            // 2. Check semantic version from tag or title
            if (!release.remoteVersionName.isNullOrBlank()) {
                val cmp = compareSemanticVersions(release.remoteVersionName, currentVersionName)
                if (cmp > 0) return true
                if (cmp < 0) return false
            }

            // 3. Fallback for build-numbered releases (e.g., debug-apk-build-12-1):
            //    Check whether release was published after this APK was installed/updated
            if (release.publishedAtMillis > 0L && installedUpdateTimeMs > 0L) {
                return release.publishedAtMillis > (installedUpdateTimeMs + TIMESTAMP_GRACE_MS)
            }

            return false
        }

        internal fun parseReleaseJson(jsonString: String): ReleaseInfo? {
            val json = JSONObject(jsonString)
            if (json.optBoolean("draft", false)) return null

            val tagName = json.optString("tag_name", "").trim()
            if (tagName.isEmpty()) return null

            val title = json.optString("name", "").trim().ifEmpty { tagName }
            val body = json.optString("body", "").trim()
            val publishedAtIso = json.optString("published_at", "").trim()
            val publishedAtMillis = parseIsoTimestamp(publishedAtIso)
            val htmlUrl = json.optString("html_url", "").trim()
                .takeIf { isTrustedGitHubUrl(it) } ?: FALLBACK_RELEASES_PAGE_URL

            var downloadUrl = FALLBACK_DOWNLOAD_URL
            var assetSizeBytes = 0L

            val assets = json.optJSONArray("assets")
            if (assets != null) {
                for (i in 0 until assets.length()) {
                    val asset = assets.optJSONObject(i) ?: continue
                    val assetName = asset.optString("name", "")
                    val candidateUrl = asset.optString("browser_download_url", "")
                    if (assetName.endsWith(".apk", ignoreCase = true) && isTrustedGitHubUrl(candidateUrl)) {
                        downloadUrl = candidateUrl
                        assetSizeBytes = asset.optLong("size", 0L)
                        break
                    }
                }
            }

            val remoteVersionName = extractSemanticVersion(tagName)
                ?: extractSemanticVersion(title)
            val remoteVersionCode = extractVersionCode(body)
            val remoteSha256 = extractSha256(body)

            return ReleaseInfo(
                tagName = tagName,
                title = title,
                releaseNotes = body,
                publishedAtIso = publishedAtIso,
                publishedAtMillis = publishedAtMillis,
                downloadUrl = downloadUrl,
                releasePageUrl = htmlUrl,
                assetSizeBytes = assetSizeBytes,
                remoteSha256 = remoteSha256,
                remoteVersionName = remoteVersionName,
                remoteVersionCode = remoteVersionCode
            )
        }
    }

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getCurrentVersionName(): String = BuildConfig.VERSION_NAME

    fun getCurrentVersionCode(): Int = BuildConfig.VERSION_CODE

    fun isTagDismissed(tagName: String): Boolean {
        return prefs.getString(KEY_DISMISSED_TAG, null) == tagName
    }

    fun markTagDismissed(tagName: String) {
        prefs.edit().putString(KEY_DISMISSED_TAG, tagName).apply()
    }

    suspend fun checkForLatestRelease(isManual: Boolean): UpdateUiState = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(LATEST_RELEASE_API_URL)
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "BolEnglishKids/${BuildConfig.VERSION_NAME}")
                .get()
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (response.code == 404) {
                    return@withContext UpdateUiState.UpToDate(
                        currentVersion = getCurrentVersionName(),
                        latestTag = null
                    )
                }

                if (!response.isSuccessful) {
                    val errMsg = if (isManual) {
                        "अपडेट तपासता आले नाही (कोड ${response.code}). थोड्या वेळाने पुन्हा प्रयत्न करा."
                    } else {
                        ""
                    }
                    return@withContext if (isManual) UpdateUiState.Error(errMsg) else UpdateUiState.Idle
                }

                val responseBody = response.body?.string().orEmpty()
                val release = parseReleaseJson(responseBody)
                    ?: return@withContext UpdateUiState.UpToDate(
                        currentVersion = getCurrentVersionName(),
                        latestTag = null
                    )

                val installedSha = computeInstalledApkSha256()
                val installedTime = getInstalledUpdateTimeMs()

                val hasUpdate = isNewerRelease(
                    release = release,
                    currentVersionName = getCurrentVersionName(),
                    currentVersionCode = getCurrentVersionCode(),
                    installedApkSha256 = installedSha,
                    installedUpdateTimeMs = installedTime
                )

                if (hasUpdate) {
                    val shouldShowDialog = isManual || !isTagDismissed(release.tagName)
                    UpdateUiState.UpdateAvailable(
                        release = release,
                        currentVersion = getCurrentVersionName(),
                        showDialog = shouldShowDialog
                    )
                } else {
                    UpdateUiState.UpToDate(
                        currentVersion = getCurrentVersionName(),
                        latestTag = release.tagName
                    )
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Update check failed: ${e.message}")
            if (isManual) {
                UpdateUiState.Error("इंटरनेट कनेक्शन तपासा आणि पुन्हा प्रयत्न करा.")
            } else {
                UpdateUiState.Idle
            }
        }
    }

    private fun getInstalledUpdateTimeMs(): Long {
        return try {
            val pkgInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            pkgInfo.lastUpdateTime
        } catch (_: Exception) {
            0L
        }
    }

    private fun computeInstalledApkSha256(): String? {
        return try {
            val sourceDir = context.applicationInfo?.sourceDir ?: return null
            val apkFile = File(sourceDir)
            if (!apkFile.exists() || !apkFile.canRead()) return null

            val digest = MessageDigest.getInstance("SHA-256")
            FileInputStream(apkFile).use { fis ->
                val buffer = ByteArray(8192)
                var read: Int
                while (fis.read(buffer).also { read = it } != -1) {
                    digest.update(buffer, 0, read)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            null
        }
    }

    fun openExternalUrl(url: String): Boolean {
        val safeUrl = if (isTrustedGitHubUrl(url)) url else FALLBACK_RELEASES_PAGE_URL
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(safeUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "No browser available to open update URL", e)
            false
        }
    }
}
