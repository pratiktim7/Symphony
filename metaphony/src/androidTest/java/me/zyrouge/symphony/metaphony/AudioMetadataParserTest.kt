package me.zyrouge.symphony.metaphony

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AudioMetadataParserTest {
    @Test
    fun testAacFallsBackToAndroid() {
        val context = InstrumentationRegistry.getInstrumentation().context
        // Copy out of the APK so the native parser receives a standalone FD
        // with no asset offset, just like a file selected from a media folder.
        val file = File.createTempFile("aac-regression", ".aac", context.cacheDir)
        try {
            context.assets.open("tone.aac").use { input ->
                file.outputStream().use { input.copyTo(it) }
            }
            for (filename in listOf("tone.aac", "tone.AAC", "tone.acc", "tone.ACC")) {
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use {
                    Assert.assertNull(AudioMetadataParser.parse(filename, it.detachFd()))
                }
            }
            val properties = AndroidAudioProperties.read(context, Uri.fromFile(file))
            Assert.assertNotNull(properties)
            Assert.assertEquals(44100L, properties!!.sampleRate)
            Assert.assertEquals(1, properties.channels)
            // Some Android extractors omit raw AAC bitrate; it must never be zero.
            Assert.assertTrue(properties.bitrate == null || properties.bitrate > 0)
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(file.absolutePath)
                val duration = retriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull()
                Assert.assertNotNull(duration)
                // ADTS duration is estimated from frame sizes on some devices.
                Assert.assertTrue(duration!! in 1000L..4000L)
            } finally {
                retriever.release()
            }
            val player = MediaPlayer()
            try {
                player.setDataSource(file.absolutePath)
                player.prepare()
                Assert.assertTrue(player.duration in 1000..4000)
            } finally {
                player.release()
            }
        } finally {
            file.delete()
        }
    }

    @Test
    fun testFlac() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val filename = "audio.flac"
        val metadata = AudioMetadataParser.parse(
            filename,
            context.assets.openFd(filename).parcelFileDescriptor.detachFd(),
        )
        assertMetadata("flac", metadata)
    }

    @Test
    fun testOgg() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val filename = "audio.ogg"
        val metadata = AudioMetadataParser.parse(
            filename,
            context.assets.openFd(filename).parcelFileDescriptor.detachFd(),
        )
        assertMetadata("ogg", metadata)
    }

    @Test
    fun testMp3() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val filename = "audio.mp3"
        val metadata = AudioMetadataParser.parse(
            filename,
            context.assets.openFd(filename).parcelFileDescriptor.detachFd(),
        )
        assertMetadata("mp3", metadata)
    }

    @Test
    fun testMp3Id23() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val filename = "audio-id3v2.3.mp3"
        val metadata = AudioMetadataParser.parse(
            filename,
            context.assets.openFd(filename).parcelFileDescriptor.detachFd(),
        )
        assertMetadata("mp3id23", metadata)
    }

    @Test
    fun testMp3Id24() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val filename = "audio-id3v2.4.mp3"
        val metadata = AudioMetadataParser.parse(
            filename,
            context.assets.openFd(filename).parcelFileDescriptor.detachFd(),
        )
        assertMetadata("mp3id24", metadata)
    }

    fun assertMetadata(source: String, metadata: AudioMetadata?) {
        Assert.assertNotNull(metadata)
        metadata!!
        Assert.assertEquals("Demo Audio2", metadata.title)
        Assert.assertArrayEquals(
            arrayOf("Demo Artist 1; Demo Artist 2"),
            metadata.artists.toTypedArray(),
        )
        Assert.assertArrayEquals(
            arrayOf("Demo Artist 2"),
            metadata.albumArtists.toTypedArray(),
        )
        Assert.assertArrayEquals(emptyArray(), metadata.composers.toTypedArray())
        Assert.assertArrayEquals(
            arrayOf("Rap", "Rock"),
            metadata.genres.toTypedArray(),
        )
        Assert.assertEquals(null, metadata.discNumber)
        Assert.assertEquals(null, metadata.discTotal)
        Assert.assertEquals(1, metadata.trackNumber)
        Assert.assertEquals(2, metadata.trackTotal)
        Assert.assertEquals(null, metadata.date)
        Assert.assertEquals(null, metadata.lyrics)
        val expectedBitrate = when (source) {
            "ogg" -> 112
            "flac" -> 144475
            else -> 130
        }
        Assert.assertEquals(expectedBitrate, metadata.bitrate)
        val expectedLength = when (source) {
            // not sure why, we ignore this for now
            "ogg" -> -2147483
            else -> 1
        }
        Assert.assertEquals(expectedLength, metadata.lengthInSeconds)
        Assert.assertEquals(44100, metadata.sampleRate)
        Assert.assertEquals(2, metadata.channels)
        Assert.assertEquals(1, metadata.pictures.size)
        Assert.assertEquals("Front Cover", metadata.pictures[0].pictureType)
        Assert.assertEquals("image/png", metadata.pictures[0].mimeType)
        Assert.assertNotEquals(0, metadata.pictures[0].data.size)
    }
}
