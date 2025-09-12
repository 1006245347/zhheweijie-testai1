import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

dependencies {
    implementation(projects.shared)
    implementation(compose.desktop.currentOs)

    //竟然把desktop模块生成代码了/build/generated/source/
//    kapt(libs.objects.processor)
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
            description = "A chat-prompt based app for Android, iOS, Desktop, Linux."
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
                shortcut = true
                iconFile.set(iconsRoot.resolve("launcher_icons/linuxos.png"))
                modules("jdk.security.auth") //来自FileKit
            }

            windows {
                iconFile.set(iconsRoot.resolve("launcher_icons/windowsos.ico"))
                upgradeUuid = "31575EDF-D0D5-4CEF-A4D2-7562083D6D89"
                menuGroup = packageName
                perUserInstall = true
                shortcut = true

                menuGroup = "AI"
                dirChooser = true
            }

            macOS {
                iconFile.set(iconsRoot.resolve("launcher_icons/macos.icns"))
            }
        }
    }
}

tasks.withType(type = org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
    compilerOptions{
        jvmTarget.set(JvmTarget.JVM_17)}
}
//
//tasks.withType(type = org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask::class) {
//    compilerOptions{ jvmTarget.set(JvmTarget.JVM_17)}
//}
