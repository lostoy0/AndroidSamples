# Gradle 构建插件

`buildSrc` 是 Gradle 自动编译并加入构建脚本类路径的辅助构建。`custom-plugin` 对应 `src/main/kotlin/com/lostoy/gradle/plugin/CustomPlugin.kt`，由 `mobile/build.gradle` 应用。它在项目配置期间输出项目名，可用来查看自定义插件如何接入 Android 模块。

从仓库根目录运行以下命令。Gradle 会先编译插件，再配置和构建手机端。

```sh
./gradlew :mobile:assembleDebug
```

插件使用 Kotlin 编写，JVM 字节码目标为 17。
