# Android 共享库

`common` 是给 `mobile` 使用的 Android 库。源码在 `src/main/java/com/lostoy/android/common/`。

`deviceid.AdvertisingIdClient` 演示通过 Binder 访问 Google Play 服务的广告标识接口，应在后台线程调用。`deviceid.AdvertisingIdClientX` 演示 AndroidX Ads Identifier 的异步接口。返回结果取决于设备是否具备相应服务。

## 构建与验证

```sh
./gradlew :common:assembleDebug
./gradlew :common:testDebugUnitTest :common:connectedDebugAndroidTest
```

实际使用入口在 [mobile](../mobile/README.md) 的「设备」Tab。选中「GAID 获取」可以触发读取，结果或错误通过 Logcat 查看。
