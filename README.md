# AndroidSamples

`mobile` contains the handset samples and the Native/Flutter demos. `tv` and
`tvappdemo` contain the Android TV samples.

## Build and run the hybrid handset app

Use JDK 17, Android SDK 36, NDK `28.2.13676358`, CMake `3.22.1`, and
Flutter `3.44.7`. From the AndroidSamples checkout, clone the Flutter module
into a sibling directory if you do not already have it.

```sh
git clone https://github.com/lostoy0/flutter_samples_module.git ../flutter_samples_module
```

Then prepare the Flutter module and install the Android app.

```sh
(cd ../flutter_samples_module && flutter pub get)
./gradlew :mobile:installDebug
adb shell am start -n com.lostoy.android.samples/.MainActivity
```

Gradle also accepts `-PflutterModuleDir=/path/to/flutter_samples_module`. The
module's generated `.android` directory comes from `flutter pub get`. Run
`flutter attach --app-id com.lostoy.android.samples` in the module after opening
a Flutter page in the Debug app.

The handset app runs on API 24+ with `arm64-v8a` or `armeabi-v7a`. Use an ARM
emulator. The TV build tasks also support a checkout without the Flutter
module. Install the handset and TV samples on separate targets when testing
both, since `mobile` and `tv` share the same application ID.

## Try the demos

- From **原生示例**, open **打开 Flutter 详情**. Increment the counter, open
  **打开原生详情**, then open another **打开 Flutter 详情** and **打开 Flutter 子页**.
  Back returns through the same pages and their counters.
- From **Flutter 示例**, open **在 Tab 内打开 Flutter 详情**, increment its
  counter, switch to the native tab and back. The Flutter tab keeps its own
  stack and counter. Back from its root selects the native tab.
- From **桥接通信**, call the Android device methods, send a message and check
  the reply, then subscribe to and cancel Native ticks. While the Flutter tab
  is visible, **Android → Dart 调用与消息** invokes Dart and sends a BasicMessage
  message to Dart using the tab's session.

## Add a URL destination

Register targets in `HybridSamples.createRuntime()`. Use `RouteTable.nativePage`
with an Activity launcher for Native pages. For a Flutter page, add an allowlist
entry and map the same path to a widget in the Dart module's route factory:

```java
routes.flutter("/preview/{id}", route -> route.positiveInt("id"));
```

The Dart route factory parses the URI, reads the ID and query values, and
returns the widget for `/preview/{id}`. Give the page a restoration ID if it
owns restorable UI state. The caller uses the same URL on either side.

```java
HybridRuntime.from(activity).router().open(activity, "samples://flutter/preview/42");
```

```dart
await router.open('samples://flutter/preview/42');
```

Use URI builders for dynamic values. The router validates inputs before
dispatch. A Native-originated Flutter URL opens a full-screen container.
Flutter-to-Flutter navigation pushes onto the current Dart stack. Flutter-to-Native
navigation starts an Android Activity. A successful call means the route was
accepted. These URLs are internal to the app.

## Add a bridge method group

Create a descriptor on both sides with the same method name and payload
contract. The receiving feature binds its handlers and adds its plugin to the
existing configuration. The calling feature only needs the descriptor and the
current session. Use StandardMessageCodec values on the wire.

For a Flutter-to-Android `profile.greet` method, add this group to the Android
configuration's plugin list.

```java
BridgeMethod<String, String> greet =
        new BridgeMethod<>("profile.greet", BridgeMethod::string, BridgeMethod::string);
BridgePlugin profile = () -> Collections.singletonList(
        greet.bind(name -> CompletableFuture.completedFuture("Hello, " + name)));
```

In Flutter, declare and call the matching method.

```dart
const greet = BridgeMethod<String, String>('profile.greet', stringValue, stringValue);
final greeting = await bridge.invoke(greet, 'Raymond');
```

For an Android-to-Flutter method, add a `BridgePlugin([method.bind(handler)])`
to the Dart registry and call `session.invoke(method, args)` on the Android
side. `demo.formatText` is a runnable example. Call `send(payload)` for
structured messages with a reply, and subscribe to `bridge.events` for Native
streams. Cancel a Flutter subscription when the screen leaves. Android pauses
its source when the session is in the background and closes it on detach.
Method handlers run on the platform UI thread at dispatch, so move blocking
work off that thread.

## Checks

```sh
(cd ../flutter_samples_module && flutter analyze && flutter test)
./gradlew :flutterbridge:testDebugUnitTest :mobile:testDebugUnitTest
./gradlew :mobile:assembleDebug :mobile:assembleRelease :tv:assembleDebug :tvappdemo:assembleDebug
./gradlew :mobile:lintDebug
adb logcat -s HybridRuntime:D
```

The engine log includes live engine and session counts. Closing a full-screen
Flutter page releases its engine. The Flutter tab keeps its engine while the
home Activity remains alive, and both counts return to zero after exiting it.

The existing `targetSdkVersion 30` triggers `ExpiredTargetSdkVersion` in
`:mobile:lintDebug` and the Release lint gate. To inspect the unsigned Release
APK locally while that target remains in place, run
`./gradlew :mobile:assembleRelease -x :mobile:lintVitalRelease`.
