package me.zyrouge.symphony.metaphony

object AudioFileClassifier {
    fun isAudio(filename: String, mimeType: String, hasOnlyAudioTracks: () -> Boolean): Boolean {
        val extension = filename.substringAfterLast('.', "")
        // Document providers commonly label all .mp4 files video/mp4.
        // Probe these containers even when their MIME type says audio.
        if (extension.equals("mp4", ignoreCase = true) ||
            mimeType.equals("video/mp4", ignoreCase = true) ||
            mimeType.equals("application/mp4", ignoreCase = true) ||
            mimeType.equals("audio/mp4", ignoreCase = true)
        ) {
            return hasOnlyAudioTracks()
        }
        return mimeType.startsWith("audio/", ignoreCase = true) ||
            extension.equals("aac", ignoreCase = true) ||
            extension.equals("acc", ignoreCase = true)
    }
}
