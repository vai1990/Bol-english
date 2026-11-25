package com.example.update

data class ReleaseInfo(
    val tagName: String,
    val title: String,
    val releaseNotes: String,
    val publishedAtIso: String,
    val publishedAtMillis: Long,
    val downloadUrl: String,
    val releasePageUrl: String,
    val assetSizeBytes: Long,
    val remoteSha256: String?,
    val remoteVersionName: String?,
    val remoteVersionCode: Int?
) {
    val displayVersion: String
        get() = remoteVersionName?.let { "v$it" } ?: tagName

    val formattedSize: String
        get() {
            if (assetSizeBytes <= 0L) return ""
            val mb = assetSizeBytes.toDouble() / (1024.0 * 1024.0)
            return String.format(java.util.Locale.US, "%.1f MB", mb)
        }
}

sealed class UpdateUiState {
    data object Idle : UpdateUiState()
    data object Checking : UpdateUiState()
    data class UpdateAvailable(
        val release: ReleaseInfo,
        val currentVersion: String,
        val showDialog: Boolean = true
    ) : UpdateUiState()

    data class UpToDate(
        val currentVersion: String,
        val latestTag: String? = null
    ) : UpdateUiState()

    data class Error(
        val message: String
    ) : UpdateUiState()
}
