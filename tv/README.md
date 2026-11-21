# Android TV 示例

`tv` 包含遥控器焦点演示和 Leanback 电影示例。Kotlin 源码在 `src/main/java/com/lostoy/android/samples/tv/`。

- `focus` 包含默认 TV 入口 `TvFocusActivity`、横向焦点列表和 `FocusedItemRecyclerView`。遥控器移动焦点时，列表项会缩放并调整绘制顺序。
- `leanback` 按 `browse`、`details`、`playback`、`presenter`、`catalog` 和 `error` 分包，分别对应电影浏览、详情、播放、视图绑定、样例数据与错误页。电影示例的图片和视频来自网络。

## 构建与验证

从仓库根目录执行以下命令。使用 Android TV 设备或 Android TV 模拟器，通过方向键验证焦点行为。

```sh
./gradlew :tv:assembleDebug
./gradlew :tv:installDebug
adb shell am start -n com.lostoy.android.samples/.tv.focus.TvFocusActivity
./gradlew :tv:connectedDebugAndroidTest
```

`tv` 和 `mobile` 共用应用 ID `com.lostoy.android.samples`，在同一设备上安装会互相替换。Leanback 浏览页可通过 `MovieBrowseActivityTest` 启动验证。
