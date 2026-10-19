package me.zyrouge.symphony.metaphony

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AudioFileClassifierTest {
    @Test
    fun acceptsAudioOnlyMp4RegardlessOfProviderMimeType() {
        for (mime in listOf("video/mp4", "audio/mp4", "application/mp4", "application/octet-stream")) {
            assertTrue(AudioFileClassifier.isAudio("song.MP4", mime) { true })
            assertFalse(AudioFileClassifier.isAudio("video.mp4", mime) { false })
        }
        assertTrue(AudioFileClassifier.isAudio("song", "video/mp4") { true })
    }

    @Test
    fun preservesOtherAudioFormatsWithoutProbing() {
        assertTrue(AudioFileClassifier.isAudio("song.mp3", "audio/mpeg") { error("Unexpected probe") })
        assertTrue(AudioFileClassifier.isAudio("song.AAC", "application/octet-stream") { error("Unexpected probe") })
        assertFalse(AudioFileClassifier.isAudio("cover.jpg", "image/jpeg") { error("Unexpected probe") })
    }
}
