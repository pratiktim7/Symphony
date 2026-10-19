package io.github.zyrouge.symphony.ui.view.nowPlaying

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import io.github.zyrouge.symphony.R
import io.github.zyrouge.symphony.ui.helpers.ScreenOrientation
import io.github.zyrouge.symphony.ui.helpers.ViewContext
import io.github.zyrouge.symphony.ui.view.NowPlayingData
import io.github.zyrouge.symphony.ui.view.NowPlayingDefaults
import io.github.zyrouge.symphony.ui.view.NowPlayingLyricsLayout
import io.github.zyrouge.symphony.ui.view.NowPlayingStates
import kotlinx.coroutines.flow.MutableStateFlow

internal val defaultHorizontalPadding = 20.dp

@Composable
fun NowPlayingBody(context: ViewContext, data: NowPlayingData) {
    val unlockLabel = stringResource(R.string.lockscreen_unlock)
    val lockScreenModifier = context.unlock?.let { unlock ->
        Modifier.pointerInput(unlock) {
            var distance = 0f
            detectVerticalDragGestures(
                onDragStart = { distance = 0f },
                onVerticalDrag = { change, amount -> change.consume(); distance += amount },
                onDragEnd = { if (distance < -80.dp.toPx()) unlock(); distance = 0f },
                onDragCancel = { distance = 0f },
            )
        }.semantics {
            customActions = listOf(CustomAccessibilityAction(unlockLabel) { unlock(); true })
        }
    } ?: Modifier
    val states = remember {
        NowPlayingStates(
            showLyrics = MutableStateFlow(
                data.lyricsLayout == NowPlayingLyricsLayout.ReplaceArtwork && NowPlayingDefaults.showLyrics
            ),
        )
    }

    data.run {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val orientation = ScreenOrientation.fromConstraints(this@BoxWithConstraints)

            Scaffold(
                modifier = Modifier.fillMaxSize().then(lockScreenModifier),
                topBar = {
                    if (context.unlock != null) {
                        Box(
                            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Default.Lock, stringResource(R.string.lockscreen_controls),
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else if (orientation.isPortrait) {
                        NowPlayingAppBar(context)
                    }
                },
                content = { contentPadding ->
                    Box(modifier = Modifier.padding(contentPadding)) {
                        when (orientation) {
                            ScreenOrientation.PORTRAIT -> Column(modifier = Modifier.fillMaxSize()) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .padding(bottom = 20.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    NowPlayingBodyCover(context, data, states, orientation)
                                }
                                Column {
                                    NowPlayingBodyContent(context, data)
                                    NowPlayingBodyBottomBar(context, data, states)
                                }
                            }

                            ScreenOrientation.LANDSCAPE -> Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceAround,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(top = 12.dp, bottom = 20.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    NowPlayingBodyCover(context, data, states, orientation)
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    Column {
                                        if (context.unlock == null) NowPlayingLandscapeAppBar(context)
                                        Box(modifier = Modifier.weight(1f))
                                        NowPlayingBodyContent(context, data)
                                        NowPlayingBodyBottomBar(context, data, states)
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}
