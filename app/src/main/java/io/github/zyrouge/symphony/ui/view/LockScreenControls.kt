package io.github.zyrouge.symphony.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.github.zyrouge.symphony.ui.helpers.ViewContext

/** Uses the same measured layout, artwork, controls and preferences as Now Playing. */
@Composable
fun LockScreenControls(context: ViewContext, onDismiss: () -> Unit) {
    val enabled by context.symphony.settings.lockScreenControls.flow.collectAsState()
    NowPlayingObserver(context) { data ->
        LaunchedEffect(data?.song?.id, enabled) {
            if (data == null || !enabled) onDismiss()
        }
        if (data != null && enabled) {
            // Keep navigation inside the show-when-locked activity. Reuse the real
            // queue screen, including its rows, menus and back-stack behavior.
            NavHost(context.navController, startDestination = NowPlayingViewRoute) {
                composable<NowPlayingViewRoute> { NowPlayingView(context) }
                composable<QueueViewRoute> { QueueView(context) }
            }
        }
    }
}
