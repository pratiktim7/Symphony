package io.github.zyrouge.symphony.services

import io.github.zyrouge.symphony.BuildConfig

@Suppress("ConstPropertyName")
object AppMeta {
    const val appName = "Symphony"
    const val author = "Zyrouge"
    const val githubRepositoryOwner = "pratiktim7"
    const val githubRepositoryName = "Symphony"
    const val githubProfileUrl = "https://github.com/zyrouge"
    const val githubRepositoryUrl =
        "https://github.com/$githubRepositoryOwner/$githubRepositoryName"

    const val version = "v${BuildConfig.VERSION_NAME}"
    const val githubIssuesUrl = "$githubRepositoryUrl/issues"
    const val discordUrl = "https://discord.gg/5k9Hdq7ycm "
    const val redditUrl = "https://reddit.com/r/symphony_app"
    const val contributingUrl = "https://github.com/zyrouge/symphony#contributing"

    const val packageName = "io.github.zyrouge.symphony"
    const val izzyOnDroidUrl = "https://apt.izzysoft.de/fdroid/index/apk/$packageName"
    const val fdroidUrl = "https://f-droid.org/en/packages/$packageName"
    const val playStoreUrl = "https://play.google.com/store/apps/details?id=$packageName"

}
