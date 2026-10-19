# Independent releases

Repository: https://github.com/pratiktim7/Symphony

The original Git remote has been removed. This repository starts with a fresh
source snapshot, including the local lock-screen and media changes. Original
history is retained locally on the upstream-history branch, because it includes tracked
signing material. The new main branch excludes secrets. Licenses and
attribution are retained. TagLib remains an external build submodule, not an app
update source.

Upstream stable/nightly release API requests, startup version checks, notices,
update settings, and onboarding update toggles have been removed. Saved update
preferences have no effect. Store download links were removed from settings.

Inherited stable, nightly, canary, and website publishing workflows are archived
in docs/legacy-workflows, outside the active GitHub Actions directory.

## Future update mechanism

No replacement update service is implemented yet. A Git push does not update
devices. Choose release hosting and delivery before adding the new updater.
Use your own release signing key, keep it outside Git, and retain it for future
releases. Do not use the signing material from the original repository.
Increment versionCode with each update. Choose a permanent applicationId before
the first independent release; it is currently unchanged to preserve development
installs. Updating an installed app requires a compatible application ID and
signing certificate.

Original author/community/contribution links and license notices remain as
attribution. They do not perform automatic update checks.
