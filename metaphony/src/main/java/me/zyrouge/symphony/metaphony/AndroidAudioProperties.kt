package me.zyrouge.symphony.metaphony

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri

data class AndroidAudioProperties(
    val bitrate: Long?,
    val sampleRate: Long?,
    val channels: Int?,
) {
    companion object {
        fun hasOnlyAudioTracks(context: Context, uri: Uri): Boolean {
            val extractor = MediaExtractor()
            try {
                extractor.setDataSource(context, uri, null)
                var hasAudio = false
                for (index in 0 until extractor.trackCount) {
                    val mime = extractor.getTrackFormat(index).getString(MediaFormat.KEY_MIME)
                    if (mime?.startsWith("video/") == true) return false
                    if (mime?.startsWith("audio/") == true) hasAudio = true
                }
                return hasAudio
            } finally {
                extractor.release()
            }
        }

        fun read(context: Context, uri: Uri): AndroidAudioProperties? {
            val extractor = MediaExtractor()
            try {
                extractor.setDataSource(context, uri, null)
                for (index in 0 until extractor.trackCount) {
                    val format = extractor.getTrackFormat(index)
                    if (format.getString(MediaFormat.KEY_MIME)?.startsWith("audio/") != true) {
                        continue
                    }
                    return AndroidAudioProperties(
                        bitrate = format.positiveInteger(MediaFormat.KEY_BIT_RATE)?.toLong(),
                        sampleRate = format.positiveInteger(MediaFormat.KEY_SAMPLE_RATE)?.toLong(),
                        channels = format.positiveInteger(MediaFormat.KEY_CHANNEL_COUNT),
                    )
                }
                return null
            } finally {
                extractor.release()
            }
        }

        private fun MediaFormat.positiveInteger(key: String): Int? =
            if (containsKey(key)) getInteger(key).takeIf { it > 0 } else null
    }
}
