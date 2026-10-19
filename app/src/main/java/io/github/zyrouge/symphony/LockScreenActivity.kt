package io.github.zyrouge.symphony

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import io.github.zyrouge.symphony.ui.helpers.ViewContext
import io.github.zyrouge.symphony.ui.theme.SymphonyTheme
import androidx.core.content.ContextCompat
import io.github.zyrouge.symphony.services.radio.RadioLockScreen
import io.github.zyrouge.symphony.ui.view.LockScreenControls

class LockScreenActivity : ComponentActivity() {
    private var receiverRegistered = false
    private val unlockReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_USER_PRESENT) finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val symphony = RadioLockScreen.currentOwner()
        if (symphony == null || symphony.radio.queue.currentSongId == null) {
            finish()
            return
        }
        setShowWhenLocked(true)
        enableEdgeToEdge()
        ContextCompat.registerReceiver(this, unlockReceiver,
            IntentFilter(Intent.ACTION_USER_PRESENT), ContextCompat.RECEIVER_NOT_EXPORTED)
        receiverRegistered = true
        setContent {
            val context = ViewContext(symphony, this, rememberNavController(), unlock = {
                    val keyguard = getSystemService(KeyguardManager::class.java)
                    if (keyguard.isKeyguardLocked) {
                        keyguard.requestDismissKeyguard(this,
                            object : KeyguardManager.KeyguardDismissCallback() {
                                override fun onDismissSucceeded() { finish() }
                            })
                    } else finish()
                })
            SymphonyTheme(context) {
                LockScreenControls(context, onDismiss = { finish() })
            }
        }
    }

    override fun onDestroy() {
        if (receiverRegistered) unregisterReceiver(unlockReceiver)
        super.onDestroy()
    }
}
