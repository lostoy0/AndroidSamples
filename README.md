# AndroidSamples

Android 手机、Android TV 和 Flutter 混合开发的 Kotlin 示例仓库。

## 模块

| 模块 | 内容 |
| --- | --- |
| [mobile](mobile/README.md) | 手机端五个 Tab、原生示例及 Flutter 宿主 |
| [tv](tv/README.md) | TV 遥控器焦点和 Leanback 电影示例 |
| [tvappdemo](tvappdemo/README.md) | 可独立安装的 TV 横向列表焦点示例 |
| [common](common/README.md) | 共享 Android 库，包含广告标识读取示例 |
| [flutterbridge](flutterbridge/README.md) | Flutter 容器、引擎管理、URL 路由和桥接通信 |
| [buildSrc](buildSrc/README.md) | 本仓库的 Gradle 示例插件 |

## 构建环境

使用 JDK 17 或 25、仓库自带的 Gradle Wrapper 和 Android SDK 36。通过 Android Studio 生成 `local.properties`，或设置 `ANDROID_HOME`。构建手机端还需要 NDK `28.2.13676358`、CMake `3.22.1` 和 Flutter `3.44.7`。

手机端依赖独立的 Flutter 模块。已有本地副本时，可直接准备该模块；否则将它克隆到仓库旁边。

```sh
git clone https://github.com/lostoy0/flutter_samples_module.git ../flutter_samples_module
(cd ../flutter_samples_module && flutter pub get)
./gradlew :mobile:installDebug
adb shell am start -n com.lostoy.android.samples/.home.SampleHomeActivity
```

Flutter 模块位于其他位置时，传入 `-PflutterModuleDir=/path/to/flutter_samples_module`。其 Android 集成目录 `.android/` 由 Flutter 生成。只构建 TV 示例时，可以省略 Flutter 模块。

## 验证

```sh
./gradlew :mobile:assembleDebug :tv:assembleDebug :tvappdemo:assembleDebug
./gradlew :mobile:testDebugUnitTest :common:testDebugUnitTest :flutterbridge:testDebugUnitTest :tvappdemo:testDebugUnitTest
./gradlew :mobile:connectedDebugAndroidTest
./gradlew :tv:connectedDebugAndroidTest
./gradlew :mobile:lintDebug
```

`mobile` 与 `tv` 共用应用 ID，建议使用不同的安装目标。手机端 APK 支持 `arm64-v8a` 和 `armeabi-v7a`，请选用兼容设备或 ARM 模拟器。

项目目前使用 `targetSdkVersion 30`，会触发手机端的 `ExpiredTargetSdkVersion`。仅需在本地检查未签名 Release APK 时，可以运行 `./gradlew :mobile:assembleRelease -x :mobile:lintVitalRelease`。
