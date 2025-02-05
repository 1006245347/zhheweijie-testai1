import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.hwj.ai.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.hwj.ai.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    signingConfigs {
        getByName("debug") {
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            storeFile = file("../gree_pad_app.jks")
            storePassword = "123456"
            keyAlias = "gree"
            keyPassword = "123456"
        }

        register("release") {
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            storeFile = file("../gree_pad_app.jks")
            storePassword = "123456"
            keyAlias = "gree"
            keyPassword = "123456"
        }
    }


    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }

    //自定义打包文件名
    afterEvaluate {
        tasks.named("assembleRelease") {
            finalizedBy("copyAndRenameApkTask")
        }
    }
}

val copyAndRenameApkTask by tasks.registering(Copy::class) {
    val config = project.android.defaultConfig
    val versionName = config.versionName
    val versionCode = config.versionCode
    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
    val createTime = LocalDateTime.now().format(formatter)
    val gitHash = providers.exec {
        commandLine("git", "rev-parse","--short","HEAD")
    }.standardOutput.asText.get()
    val destDir = File(rootDir, "apkBackup/compose_${versionName}")
    from("release/androidApp-release.apk")
    into(destDir)
    rename { _ -> "compose_ark_${versionName}_${versionCode}_${createTime}.apk" }
    doLast {
        File(destDir, "App上传配置.txt").outputStream().bufferedWriter().use {
            it.appendLine("版本号:${versionCode}")
                .appendLine("版本名称:${versionName}")
                .appendLine("软件名称:格力方舟大全")
                .appendLine("软件包名:com.lyentech.ark")
                .appendLine("版本说明:kotlin multiplatform、compose-multiplatform")
                .appendLine("发布时间:${createTime}")
                .appendLine("git记录:${gitHash}")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.appCompat)
}