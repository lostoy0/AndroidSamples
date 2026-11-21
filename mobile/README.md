# 手机端示例

`mobile` 是手机端应用。首页由 `home.SampleHomeActivity` 承载，`catalog.Sample` 定义示例名称、URL 路径和详情页。

| Tab | 示例 |
| --- | --- |
| 组件 | 输入框与焦点、RecyclerView 动画、自定义进度条 |
| 布局 | ConstraintLayout Flow |
| 设备 | GAID、OAID |
| 工具 | JNI 调用、数值运算 |
| Flutter | 嵌入式 Flutter 首页、全屏 Flutter 详情、桥接通信 |

Kotlin 源码在 `src/main/java/com/lostoy/android/samples/`。目录按 `home`、`catalog`、`widgets`、`layout`、`device`、`tools` 和 `hybrid` 划分。`app.SamplesApplication` 初始化路由与桥接配置。JNI 实现在 `src/main/cpp/`，由 `CMakeLists.txt` 构建。

## 构建与运行

从仓库根目录执行以下命令。环境和 Flutter 模块路径见[仓库 README](../README.md)。

```sh
./gradlew :mobile:installDebug
adb shell am start -n com.lostoy.android.samples/.home.SampleHomeActivity
./gradlew :mobile:testDebugUnitTest :mobile:connectedDebugAndroidTest
```

在 Flutter Tab 中打开页内详情并切换到其他 Tab，切回时可继续使用原导航栈。点「打开 Flutter 详情」进入独立 Activity，再点「打开原生详情」可验证跨容器路由。「Android → Dart 调用与消息」可验证嵌入式引擎的桥接回包。

Debug 模式下打开 Flutter 页面后，可在 Flutter 模块中运行 `flutter attach --app-id com.lostoy.android.samples`。引擎生命周期、路由约定和启动测量见 [flutterbridge README](../flutterbridge/README.md)。
