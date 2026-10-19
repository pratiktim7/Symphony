package io.github.zyrouge.symphony.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.zyrouge.symphony.ui.helpers.ViewContext

@Composable
fun IntroductoryDialog(
    context: ViewContext,
    onDismissRequest: () -> Unit,
) {
    ScaffoldDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("\uD83D\uDC4B " + context.symphony.t.HelloThere)
        },
        content = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    context.symphony.t.IntroductoryMessage.trim(),
                    modifier = Modifier.padding(16.dp, 12.dp),
                )
            }
        }
    )
}
