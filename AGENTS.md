# Repository Guidelines

## Project Structure & Module Organization

- `mobile/` contains handset samples, including RecyclerView, custom widgets, and device ID experiments. Native code lives in `mobile/src/main/cpp/` and builds through `mobile/CMakeLists.txt`. Keep runtime assets in `mobile/src/main/assets/`.
- `tv/` contains the Android TV Leanback samples. `tvappdemo/` is a standalone TV RecyclerView demo.
- `common/` is the shared Android library used by `mobile`. `buildSrc/` contains the Gradle plugin applied by `mobile`.
- Put Java and Kotlin sources under each module’s `src/main/java/` and Android resources under `src/main/res/`.

## Build, Test, and Development Commands

Use JDK 17 or 25 and the checked-in Gradle 9.1.0 wrapper. Install Android SDK Platform 36 and the NDK and CMake versions pinned in `mobile/build.gradle`. Set `ANDROID_HOME` or let Android Studio generate a local `local.properties` with the SDK path.

- `./gradlew :mobile:assembleDebug` builds the mobile debug APK. Substitute `:tv` or `:tvappdemo` to build a TV sample.
- `./gradlew :mobile:installDebug` installs the app on a connected device or emulator that supports `arm64-v8a` or `armeabi-v7a`. To launch a TV sample, select its module and an Android TV target in Android Studio. `mobile` and `tv` share an application ID, so installing one can replace the other.
- `./gradlew :mobile:lintDebug` runs Android Lint for the mobile debug variant.

## Coding Style & Naming Conventions

Use four spaces in Java, Kotlin, and Gradle files. Follow existing package names, use UpperCamelCase for types and lowerCamelCase for methods and variables, and use lowercase snake_case for Android resources, such as `fragment_tv_demo.xml`.

## Testing Guidelines

Put JUnit 4 unit tests in `src/test/java/` and AndroidX instrumentation tests in `src/androidTest/java/` for `mobile`, `common`, and `tvappdemo`. Name test classes `*Test` and methods by behavior and expected result, such as `addition_isCorrect`. Run `./gradlew :mobile:testDebugUnitTest` locally and `./gradlew :mobile:connectedDebugAndroidTest` with a compatible device or emulator. Use the same tasks for `common` and `tvappdemo` with their module paths. The `tv` module contains a legacy `ApplicationTestCase`. The build defines no coverage threshold.

## Commit & Pull Request Guidelines

Git history uses short, imperative subjects. Name the affected sample and behavior, for example `fix TV row focus`. In pull requests, summarize the change, list the checks you ran, link any related issue, and attach screenshots or a recording for visible UI changes.
