import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinxSerialization)//kotlinxSerialization
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        compilations.all {
//            kotlinOptions {
//                jvmTarget = "17"
//            }
            compileTaskProvider.configure{
                compilerOptions{
                    jvmTarget.set(JvmTarget.JVM_17)
                }
             }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material) // https://github.com/adrielcafe/voyager/issues/185
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)

            //支持多平台的网络库
            implementation(libs.ktor.client.core) //网络请求
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.logging)

            implementation(libs.kotlin.datetime)

            //依赖注入
            implementation(libs.koin.core)

            //另一个导航
            implementation(libs.precompose.navigator)
            implementation(libs.precompose.koin)
            implementation(libs.precompose.viewmodel)

            //异步图片加载   //这个版本还缺其他？
            implementation(libs.coil3.core)
            implementation(libs.coil3.ktor)
            implementation(libs.coil3.compose) //这个地址太坑了，官网没更新出来

            //异步协程库
            implementation(libs.kotlinx.coroutines.core)

            //权限 compose multiplatform https://github.com/icerockdev/moko-permissions
            implementation(libs.mokopermission)
            implementation(libs.mokopermission.compose)
            implementation(libs.stately.common)
            implementation(libs.mokoMvvmCore)
            implementation(libs.mokoMvvmCompose)

            //多平台uuid https://github.com/benasher44/uuid/tree/master 不知道怎么用
            implementation(libs.uuid)

            //日志库
            implementation(libs.logger.kermit)

            //key-value存储
            implementation(libs.multiplatform.settings)

            //页面自适配判断库
            implementation(libs.windowSize)

            //https://github.com/skydoves/FlexibleBottomSheet 从底部弹窗
            implementation(libs.bottomSheet)

            //分页库
            implementation(libs.paging.compose)

            //网络流的数据可存FileSystem
            implementation(libs.okio.core)

            //文件选择器
            implementation(libs.file.picker)
        }
//        commonTest.dependencies {
//            implementation(libs.kotlin.test)
//        }

        androidMain.dependencies {
            //引入本地Android aar库
            implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
//            implementation(files("../androidApp/libs/TbsFileSdk_dwg_universal_release_1.0.5.6000030.20231109143411.aar"))

//            api(libs.compose.ui)
//            api(libs.compose.ui.tooling.preview)
//            api(libs.androidx.activity.compose)
//            api(libs.appCompat)
//            api(libs.compose.material3)

            implementation(libs.androidx.perference)

            // Koin
            api(libs.koin.android)
            api(libs.koin.androidx.compose)

            //android平台引擎
            implementation(libs.ktor.client.android)

            //mmvm
            implementation(libs.androidx.lifecycle)
            implementation(libs.lifecycle.extension)

            api(libs.core.splashscreen)

            implementation(libs.datastore.preferences)

            //图片库加载，超长图moo
            implementation(libs.coil3.video)
            implementation(libs.coil3.gif)

            //远程日志上报
            implementation(libs.android.bugly)

            //权限申请
            implementation(libs.permissionX.android)

            // SQL
            implementation(libs.android.driver)
            implementation(libs.coroutines.extensions)
        }

        iosMain.dependencies {
            //网络库，提供iOS平台引擎
            implementation(libs.ktor.client.ios)

            //sql
            api(libs.native.driver)
        }

        targets.all {
//            compilations.all {
//                compilerOptions.configure {
//                    freeCompilerArgs.add("-Xexpect-actual-classes")
//                }
//            }
            compilations.all{
                compileTaskProvider.configure{
                    compilerOptions{
                        freeCompilerArgs.add("-Xexpect-actual-classes")
                    }
                }
            }
        }
    }

    cocoapods {  //cocoapods类似gradle管理包构建依赖，这里集成日志库
        version = "1.0"
        summary = "Sample for Kmm"
        homepage = "https://www.touchlab.co"
        framework {
            baseName = "LiteLibs"
            isStatic = true

            // Only if you want to talk to Kermit from Swift
            export("co.touchlab:kermit-simple:2.0.3")
        }
    }
}

android {
    namespace = "com.hwj.ai"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility =JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }

//    composeOptions {
//        kotlinCompilerExtensionVersion = libs.versions.kotlinComposeCompiler.get()
//    }

    kotlin {
        jvmToolchain(17)
    }
}


buildkonfig {
    packageName = "com.hwj.ai"

    defaultConfigs {
        buildConfigField(
            STRING,
            "OPENAI_API_KEY",
//            gradleLocalProperties(project.rootDir, providers).getProperty("openai_api_key")//openai_api_key
//            gradleLocalProperties(project.rootDir, providers).getProperty("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")//openai_api_key
//            "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"
            "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"
        )

        buildConfigField(
            STRING,
            "ADMOB_REWARDED_AD_ID",
//            gradleLocalProperties(project.rootDir, providers).getProperty("admob_rewarded_ad_id")//admob_rewarded_ad_id
            "12"
        )
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.hwj.ai")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
        }
    }
}

kotlin.sourceSets.all {
    languageSettings.optIn("com.russhwolf.settings.ExperimentalSettingsImplementation")
    languageSettings.optIn("com.russhwolf.settings.ExperimentalSettingsApi")
    languageSettings.optIn("com.aallam.openai.api.BetaOpenAI")
    languageSettings.optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
    languageSettings.optIn("androidx.compose.material3.ExperimentalMaterial3Api")
    languageSettings.optIn("androidx.compose.ui.ExperimentalComposeUiApi")
    languageSettings.optIn("androidx.compose.foundation.ExperimentalFoundationApi")
    languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
    languageSettings.optIn("kotlin.time.ExperimentalTime")
}

