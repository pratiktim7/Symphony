# Lock screen controls

Enable **Settings > Player > Lock screen controls**. Android 10 and later require
the user to grant **Display over other apps** in system settings; returning without
granting access leaves the feature off. Start a song, lock the device, and wake it.
The same settings section provides a preview while a track is loaded.

The screen reuses Now Playing's artwork, metadata, favorite button, playback
controls, seek bar and bottom controls, with no scrolling page. A small centered
lock icon identifies the screen.
Artwork takes the remaining height so the controls fit the screen. Symphony's
theme, accent color, Material You, fonts, content/font scaling, additional info,
control layout and seek-control preferences all apply.

Swipe upward to request Android's keyguard unlock. Cancelling authentication
leaves the lock screen in place; successful unlock closes it. An accessibility
Unlock device action provides an alternative to swiping. The Playing X of Y
button opens the same full-screen Queue view as Now Playing, initially positioned
at the current song. It includes artwork rows, track numbers, selection checkboxes,
song menus, save/delete/clear actions and current-track highlighting. Tapping a
song starts it and keeps the queue open, just like the normal queue. The down arrow
or system Back returns to playback controls without unlocking.
Both three-dot menus open directly: favorites, queue/playlist actions, song details,
sleep timer, speed, pitch and pause-at-end controls are available while locked.
Library page navigation still requests unlock; external sharing/equalizer apps
remain subject to Android's own lock-screen behavior. Normal Android media
notification controls remain available.

## Integration

`RadioLockScreen` registers for screen-on events during the existing Radio
lifetime and retains only a weak reference to its Symphony owner. The separate,
non-exported activity uses that player rather than creating another ViewModel.
Cold activity recreation without a live playback owner exits immediately.
Automatic entry requires enabled controls, a playing track, a locked device,
normal audio mode, and (API 29+) overlay permission. A paused screen stays usable,
but paused playback does not automatically open a new screen.

This is an original Compose implementation informed by the supplied static
Musicolet behavior report. It does not copy decompiled code or use the reference
app's full-screen notification/helper-service launch mechanism. Modern Android
reserves that notification permission for calling/alarm use cases. OEM background
launch restrictions can still affect automatic presentation.

## Device verification

- With the feature off, waking a locked device must not open custom controls.
- Enable it, deny overlay access, then retry and grant access. Check the saved toggle.
- Play music, lock/wake, and verify metadata, artwork and progress follow playback.
- Pause/resume, skip while paused, seek to both ends, and test configured rewind/forward.
- Change tracks while dragging the slider; releasing must not seek the new track.
- Test first/last queue entries, missing artwork, long titles, landscape and large text.
- Swipe up on artwork and on the controls area. Unlock with a PIN/biometric and
  confirm controls close. Cancel authentication and confirm the device remains locked.
- Compare against Now Playing with black/light themes, custom accents, fonts,
  scaling, all three control layouts and seek controls enabled/disabled. Verify
  the screen fits without page scrolling and horizontal seeking does not unlock.
- Open Playing X of Y and compare its full-screen layout with the normal queue.
  Scroll, select a song and confirm playback changes without unlocking or closing
  the queue. Back/the down arrow must return to playback controls. Check song menus,
  selection removal, saving the queue and clearing playback. Check both three-dot menus
  and their in-app dialogs while locked, and verify the centered lock indicator.
- Stop playback or clear the queue; controls must close. Kill/recreate the process;
  an old activity must not create a second player or expose the main library.
- While paused, unlocked, or in a call, waking must not automatically show controls.
- Test Android 9 and a recent Android version on physical devices; JVM tests verify
  launch eligibility, not OS/OEM presentation or keyguard behavior.

Platform reference: https://developer.android.com/guide/components/activities/background-starts
