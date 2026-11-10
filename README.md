# EnglishMate Android

A starter Android app for learning English vocabulary with Vietnamese translations.

## Features

- English–Vietnamese vocabulary flashcards
- Example sentences for each word
- English pronunciation using Android Text-to-Speech
- Multiple-choice vocabulary quiz with score tracking
- Material 3 UI built with Jetpack Compose

## Tech stack

- Kotlin
- Android Gradle Plugin 8.7.3
- Jetpack Compose + Material 3
- Android minSdk 24, compileSdk 35

## Open and run

1. Open this repository in Android Studio (Ladybug or newer recommended).
2. Allow Gradle to sync and download dependencies.
3. Select the `app` run configuration and an Android device/emulator.
4. Press **Run**.

The app uses the device's English Text-to-Speech engine for pronunciation. Install an English voice in Android settings if speech is unavailable.

## Project structure

```
app/src/main/
  AndroidManifest.xml
  java/com/englishmate/MainActivity.kt
build.gradle.kts
settings.gradle.kts
```

This is an MVP starter. Vocabulary is currently stored locally in the app source; persistence, spaced repetition, and expanded content can be added next.
