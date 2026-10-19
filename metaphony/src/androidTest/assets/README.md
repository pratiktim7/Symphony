`tone.aac` is a generated two-second 440 Hz sine wave (AAC-LC in ADTS).
It exercises the Android metadata fallback and player preparation for raw AAC.

To regenerate with FFmpeg:

```sh
ffmpeg -f lavfi -i "sine=frequency=440:duration=2" -c:a aac -b:a 64k -f adts tone.aac
```


MP4 scanner fixtures are synthetic one-second tones:

- `audio-only-opus.mp4`: mono Opus at 48 kHz in MP4.
- `audio-only-opus-cover.mp4`: the same audio with an attached blue JPEG cover.
- `audio-video.mp4`: AAC audio plus a real H.264 video track.

These test accepting audio-only MP4 (including cover art) while excluding video.
The user's song is not included in the fixtures.
