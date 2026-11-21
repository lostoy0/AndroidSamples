# 独立 TV 焦点示例

`tvappdemo` 演示横向 RecyclerView 的遥控器焦点移动、放大动画与绘制顺序。应用 ID 是 `com.example.tvappdemo`，可以与其他示例并存。

`focus.TvFocusActivity` 承载 `TvFocusFragment`，后者绑定横向列表。`FocusedItemRecyclerView` 把获得焦点的条目放到最后绘制。布局和选择器位于 `src/main/res/`。

## 构建与验证

```sh
./gradlew :tvappdemo:installDebug
adb shell am start -n com.example.tvappdemo/.focus.TvFocusActivity
./gradlew :tvappdemo:testDebugUnitTest :tvappdemo:connectedDebugAndroidTest
```

在 Android TV 上启动后，用方向键在按钮与列表之间移动，观察列表条目的放大和恢复。设备端测试会启动 Activity 并检查示例列表能否正常加载。
