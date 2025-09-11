import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinKapt)
}

//处理依赖库重复问题
configurations.all {
    resolutionStrategy.force("androidx.compose.ui:ui-test-junit4-android:1.7.6")
        .force("androidx.compose.ui:ui-test-android:1.7.6")

    // FIXME exclude netty from Koog dependencies?
    exclude(group = "io.netty", module = "*")
}

android {
    namespace = "com.hwj.ai.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.hwj.ai.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = properties["version"] as String

//        ndk { //设置支持的SO库架构（开发者可以根据需要，选择一个或多个平台的so）
//            abiFilters += listOf("armeabi", "armeabi-v7a", "arm64-v8a")
//        }
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
        getByName("debug") {
            //签名
            signingConfig = signingConfigs.getByName("debug")
        }
        register("alpha") {
            //继承debug配置
            initWith(getByName("debug"))
            //混淆
//            isMinifyEnabled = true //有混淆无法编译？
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
            //移除无用的resource文件
//            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("release") {
            //继承alpha配置
            initWith(getByName("alpha"))
            //关闭debug
//            isDebuggable = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    }
    kotlinOptions {
        jvmTarget=libs.versions.java.get()
    }
    kotlin {
        jvmToolchain(libs.versions.java.get().toInt())
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
        commandLine("git", "rev-parse", "--short", "HEAD")
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
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.appCompat)
    implementation(libs.compose.material3)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    //kmp不支持，原生只能放这,放这里shared/jvmMain里面没法识别呀
//    debugImplementation(libs.objects.browser)
//    releaseImplementation(libs.objects.android)
//    kapt(libs.objects.processor)
}