# Flutter 混合桥接库

`flutterbridge` 将独立的 [Flutter 示例模块](https://github.com/lostoy0/flutter_samples_module) 接入 Android 宿主。源码位于 `src/main/java/com/example/flutter/bridge/`。

| 类型 | 职责 |
| --- | --- |
| `HybridRuntime` | 在进程内创建和缓存 `FlutterEngineGroup`，为每个引擎建立会话 |
| `HybridFlutterActivity` | 承载全屏 Flutter 页面，每个 Activity 从 Group 创建独立引擎 |
| `HybridTabFragment` | 承载嵌入首页的 Flutter Tab，切换 Tab 时保留其引擎和导航栈 |
| `HybridRouter`、`RouteTable` | 校验和匹配 `samples://` URL，并交给原生或 Flutter 容器 |
| `BridgeSession`、`BridgeMethod`、`BridgePlugin` | 管理每个引擎的调用、消息、事件订阅与关闭 |

## 构建与验证

先按[仓库 README](../README.md)准备 Flutter 模块，再从仓库根目录执行以下命令。

```sh
./gradlew :flutterbridge:assembleDebug
./gradlew :flutterbridge:testDebugUnitTest :flutterbridge:connectedDebugAndroidTest
adb logcat -s HybridRuntime:D
```

关闭全屏 Flutter 容器后，其引擎和会话会释放。Flutter Tab 的引擎随首页存活，退出首页后可在 `HybridRuntime` 日志中观察会话数和引擎数回到零。Flutter 在同一个 Group 中有存活引擎时可以派生新引擎，清空 Group 后再创建会重新走引擎初始化路径。

## 添加路由与桥接方法

Android 的示例 URL 在 `mobile` 的 `HybridSampleRoutes.createRuntime()` 注册。用 `RouteTable.nativePage` 添加原生目的地。添加 Flutter 页面时，注册路径与参数校验，并在 Dart 侧配置同名路径。

```kotlin
routes.flutter("/preview/{id}") { it.positiveInt("id") }
HybridRuntime.from(activity).router().open(activity, "samples://flutter/preview/42")
```

用 URI 构建器写入动态参数。有需要恢复的页面状态时，为 Dart 页面设置恢复 ID。Android 打开 Flutter URL 时启动全屏 Activity，Flutter 内部打开 Flutter URL 时在当前 Dart 导航栈中前进。Flutter 跳到原生 URL 时由 Android 启动相应 Activity。路由调用成功表示宿主已接受请求，页面显示由 Flutter 渲染回调确定。

桥接方法在两端使用相同的方法名和参数约定。Android 侧可用下面的方式绑定处理器，并把插件加入 `HybridRuntime.Configuration`。

```kotlin
val greet = BridgeMethod("profile.greet", BridgeMethod.Companion::string, BridgeMethod.Companion::string)
val profile = BridgePlugin {
    listOf(greet.bind { name -> CompletableFuture.completedFuture("Hello, $name") })
}
```

Flutter 侧可声明并调用对应的方法。

```dart
const greet = BridgeMethod<String, String>('profile.greet', stringValue, stringValue);
final greeting = await bridge.invoke(greet, 'Raymond');
```

调用方使用当前引擎的 `session.invoke(method, args)`。双向结构化消息用 `send(payload)`，Flutter 用 `bridge.events` 订阅原生事件。通道使用 `StandardMessageCodec`。处理器在 Android 主线程接收调用，耗时操作应切换到后台线程。

Flutter 页面退出时应取消事件订阅。容器进入后台时会暂停原生事件源，销毁时会关闭会话。

## Flutter 引擎与页面首帧实测

首个引擎与后续引擎的耗时、路由到首帧的占比和测试口径见[Flutter 引擎启动与页面首帧实测](../docs/flutter-engine-startup.md)。
