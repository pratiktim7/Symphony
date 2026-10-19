package io.github.zyrouge.symphony.services.radio

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import io.github.zyrouge.symphony.LockScreenActivity
import io.github.zyrouge.symphony.Symphony
import io.github.zyrouge.symphony.utils.Logger
import java.lang.ref.WeakReference

/** Shares the existing playback owner; the lock screen must never create another player. */
class RadioLockScreen(private val symphony: Symphony) : BroadcastReceiver() {
    private var registered = false

    fun start() {
        if (registered) return
        owner = WeakReference(symphony)
        ContextCompat.registerReceiver(
            symphony.applicationContext, this, IntentFilter(Intent.ACTION_SCREEN_ON),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        registered = true
    }

    fun destroy() {
        if (registered) symphony.applicationContext.unregisterReceiver(this)
        registered = false
        if (owner?.get() === symphony) owner = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_SCREEN_ON) return
        if (!LockScreenLaunchPolicy.canLaunch(
            enabled = symphony.settings.lockScreenControls.value,
            playing = symphony.radio.isPlaying,
            hasSong = symphony.radio.queue.currentSongId != null,
            locked = context.getSystemService(KeyguardManager::class.java).isKeyguardLocked,
            normalAudioMode = context.getSystemService(AudioManager::class.java).mode == AudioManager.MODE_NORMAL,
            sdk = Build.VERSION.SDK_INT,
            overlayAllowed = Settings.canDrawOverlays(context),
        )) return
        try {
            context.startActivity(Intent(context, LockScreenActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (error: RuntimeException) {
            Logger.warn("RadioLockScreen", "Unable to display lock screen controls", error)
        }
    }

    companion object {
        private var owner: WeakReference<Symphony>? = null
        fun currentOwner(): Symphony? = owner?.get()
    }
}
