package io.github.zyrouge.symphony.ui.helpers

import androidx.navigation.NavHostController
import androidx.activity.ComponentActivity
import io.github.zyrouge.symphony.Symphony

data class ViewContext(
    val symphony: Symphony,
    val activity: ComponentActivity,
    val navController: NavHostController,
    val unlock: (() -> Unit)? = null,
) {
    companion object {
        fun <T> parameterizedFn(fn: (ViewContext) -> T) = fn
    }
}
