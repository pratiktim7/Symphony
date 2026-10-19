package io.github.zyrouge.symphony.services.radio

internal object LockScreenLaunchPolicy {
    fun canLaunch(
        enabled: Boolean,
        playing: Boolean,
        hasSong: Boolean,
        locked: Boolean,
        normalAudioMode: Boolean,
        sdk: Int,
        overlayAllowed: Boolean,
    ): Boolean = enabled && playing && hasSong && locked && normalAudioMode &&
        (sdk < 29 || overlayAllowed)
}
