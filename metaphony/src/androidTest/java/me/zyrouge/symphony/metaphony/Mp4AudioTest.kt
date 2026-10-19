package me.zyrouge.symphony.metaphony

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class Mp4AudioTest {
    @Test
    fun acceptsOpusWithOrWithoutArtworkAndRejectsVideo() {
        val context = InstrumentationRegistry.getInstrumentation().context
        for ((asset, expected) in listOf(
            "audio-only-opus.mp4" to true,
            "audio-only-opus-cover.mp4" to true,
            "audio-video.mp4" to false,
        )) {
            val file = File.createTempFile("mp4-regression", ".mp4", context.cacheDir)
            try {
                context.assets.open(asset).use { input ->
                    file.outputStream().use { input.copyTo(it) }
                }
                assertEquals(asset, expected, AudioFileClassifier.isAudio(file.name, "video/mp4") {
                    AndroidAudioProperties.hasOnlyAudioTracks(context, Uri.fromFile(file))
                })
                if (expected) {
                    val properties = AndroidAudioProperties.read(context, Uri.fromFile(file))
                    assertEquals(48000L, properties?.sampleRate)
                    assertEquals(1, properties?.channels)
                }
            } finally {
                file.delete()
            }
        }
    }
}
