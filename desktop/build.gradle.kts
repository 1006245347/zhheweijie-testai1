import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

dependencies {
    implementation(projects.shared)
    implementation(compose.desktop.currentOs)
}


group = "com.hwj.ai"
version = properties["version"] as String

compose.desktop {
    application {
        mainClass = "DesktopAppKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)

            packageName = "hwjAi"
            packageVersion = project.version as String
            description =
                "An  app that helps users enhance their productivity and time management skills through focused work intervals and short breaks."
            copyright = "© 2025 hwj"
            vendor = "Heweijie"

            // .gradlew suggestRuntimeModules
            modules(
                "java.instrument",
                "java.management",
                "java.prefs",
                "java.sql",
                "jdk.unsupported"
            )

            val iconsRoot = project.file("src/main/resources/drawables")

            linux {
                iconFile.set(iconsRoot.resolve("launcher_icons/linuxos.png"))
                modules("jdk.security.auth") //来自FileKit
            }

            windows {
                iconFile.set(iconsRoot.resolve("launcher_icons/windowsos.ico"))
                upgradeUuid = "31575EDF-D0D5-4CEF-A4D2-7562083D6D89"
                menuGroup = packageName
                perUserInstall = true
            }

            macOS {
                iconFile.set(iconsRoot.resolve("launcher_icons/macos.icns"))
            }
        }
    }
}

//tasks.register<Copy>("copyJacobDll") {
////    from("src/jvmMain/resources") // DLL 文件所在目录
//    from("libs")
//    include("jacob-1.21-x64.dll")  // 或者 jacob-1.19-x86.dll，取决于你的平台架构
//    into("$buildDir/libs")         // 将 DLL 复制到 build/libs 目录
//}
//
//tasks.register<JavaExec>("runDesktop"){
//    dependsOn("copyJacobDll")
//    doFirst {
//        // 设置 Java 的 library path 指定 DLL 文件的位置
//        val javaLibraryPath = System.getProperty("java.library.path")
//        System.setProperty("java.library.path", "$buildDir/libs")
//
//        // 重新加载 System.getProperty("java.library.path") 以确保生效
//        try {
//            val field = ClassLoader::class.java.getDeclaredField("sys_paths")
//            field.isAccessible = true
//            field.set(null, null)  // 清空缓存的路径
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }}
//}
