import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinxSerialization)
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
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_21)
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
            isStatic = false
//            linkerOpts("-Xbinary=bundleId=com.yourcompany.shared")
        }
    }

    jvm {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_21) //这修改jdk
                }
            }
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

            implementation(libs.androidx.lifecycle.viewmodel.compose)
//            implementation(libs.androidx.navigation.compose)
            implementation(libs.kotlinX.serializationJson)

            implementation(libs.stdlib)

            //支持多平台的网络库
            implementation(libs.ktor.client.core) //网络请求
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.logging)

            //aallam openai  https://github.com/aallam/openai-kotlin
            implementation(libs.openai.client)


            implementation(libs.kotlin.datetime)

            //依赖注入
            api(libs.koin.core) //在desktopApp引入了
            implementation(libs.koin.compose)

            //导航  https://github.com/Tlaster/PreCompose
            implementation(libs.precompose.navigator)
            implementation(libs.precompose.koin)
            implementation(libs.precompose.viewmodel)

            //异步图片加载   //这个版本还缺其他？
            implementation(libs.coil3.svg)
            implementation(libs.coil3.ktor)
            implementation(libs.coil3.compose) //这个地址太坑了，官网没更新出来

            //异步协程库
            implementation(libs.kotlinx.coroutines.core)

            //多平台uuid https://github.com/benasher44/uuid/tree/master
            implementation(libs.uuid)

            //日志库,需要初始化 https://github.com/AAkira/Napier
            api(libs.napier)

            //key-value存储
            api(libs.multiplatform.settings)
            api(libs.multiplatform.coroutines)
            api(libs.multiplatform.serialization)

            //页面自适配判断库
            implementation(libs.windowSize)

            //网络流的数据可存FileSystem
            implementation(libs.okio.core)

            //文件选择器
            api(libs.file.picker)
            api(libs.file.dialog)
            api(libs.file.dialog.compose)
            api(libs.file.coil)


//            //富文本
//            https://github.com/MohamedRejeb/compose-rich-editor
            implementation(libs.rich.editor)

            //首次引导使用 https://github.com/svenjacobs/reveal
            implementation(libs.reveal)

            //截图windows
//            implementation(libs.capture.shot)

            //分页库
            implementation(libs.paging.compose)

            implementation(libs.sqlDelight.runtime)
            implementation(libs.sqldelight.extensions)
            implementation(libs.primitive.adapters)

            //权限 compose multiplatform https://github.com/icerockdev/moko-permissions
            implementation(libs.stately.common)
            implementation(libs.mokoMvvmCore)
            implementation(libs.mokoMvvmCompose)

            implementation(libs.ktoken)


        }

        androidMain.dependencies {
            //引入本地Android aar库
            implementation(
                fileTree(
                    mapOf(
                        "dir" to "libs",
                        "include" to listOf("*.jar", "*.aar")
                    )
                )
            )
//            implementation(files("../androidApp/libs/TbsFileSdk_dwg_universal_release_1.0.5.6000030.20231109143411.aar"))

            implementation(libs.androidx.perference)
            implementation(libs.accompanist.systemUIController)
            implementation(libs.androidx.core)

            implementation(libs.mokopermission)
            implementation(libs.mokopermission.compose)
            implementation(libs.mokopermission.camera)
            implementation(libs.mokopermission.gallery)
            implementation(libs.mokopermission.storage)
            implementation(libs.mokopermission.notifications)

            // Koin
            api(libs.koin.android)
            api(libs.koin.androidx.compose)

            //android平台引擎
            implementation(libs.ktor.client.android)
            implementation(libs.ktor.client.okhttp)

            implementation(libs.androidx.lifecycle)
            implementation(libs.lifecycle.extension)

            api(libs.core.splashscreen)

            //实现本地数据存储
            implementation(libs.datastore.preferences)
            implementation(libs.multiplatform.datastore)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.coroutines)

            //图片库加载，超长图moo //新版没有：？
//            implementation(libs.coil3.video)
//            implementation(libs.coil3.gif)

            //远程日志上报
            implementation(libs.android.bugly)

            //权限申请
            implementation(libs.permissionX.android)

            // SQL
            implementation(libs.android.driver)

            //markdown,富文本  math Latex https://github.com/halilozercan/compose-richtext
            implementation(libs.richtext.core)
            implementation(libs.richtext.markdown)
            implementation(libs.richtext.mark)
            implementation(libs.richtext.material)
            implementation(libs.richtext.material3)

            //摄像头
            implementation(libs.camera.lifecycle)
            implementation(libs.camera.view)
            implementation(libs.camera.camera2)
            implementation(libs.accompanist.permissions)
            implementation(libs.kotlinx.coroutines.guava)

        }

        iosMain.dependencies {
            implementation(libs.mokopermission)
            implementation(libs.mokopermission.compose)
            implementation(libs.mokopermission.camera)
            implementation(libs.mokopermission.gallery)
            implementation(libs.mokopermission.storage)
            implementation(libs.mokopermission.notifications)

            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.coroutines)

            //网络库，提供iOS平台引擎
            implementation(libs.ktor.client.ios)

            //sql
            api(libs.native.driver)

            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
        }

        jvmMain.dependencies {
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.coroutines)

            implementation(libs.sqlite.driver)
            implementation(libs.kotlinx.coroutines.swing)
            // Toaster for Windows
            implementation(libs.toast4j)
            // JNA for Linux
            implementation("de.jangassen:jfa:1.2.0") {
                // not excluding this leads to a strange error during build:
                // > Could not find jna-5.13.0-jpms.jar (net.java.dev.jna:jna:5.13.0)
                exclude(group = "net.java.dev.jna", module = "jna")
            }

            implementation(libs.com4j)

            // JNA for Windows
            implementation(libs.jna)
            implementation(libs.jna.platform)
            //加上可以用预览注解
            implementation(compose.desktop.common)

            //markdown
            implementation(libs.richtext.core)
            implementation(libs.richtext.markdown)
            implementation(libs.richtext.mark)
            implementation(libs.richtext.material)
            implementation(libs.richtext.material3)

        }
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.withType<org.jetbrains.kotlin.gradle.plugin.mpp.Framework> {
            @OptIn(ExperimentalKotlinGradlePluginApi::class)
            transitiveExport = true
            compilations.all {
                compileTaskProvider.configure {
                    compilerOptions {//-Xexpect-actual-classes
                        freeCompilerArgs.addAll(
                            listOf(
                                "-linker-options",
                                "-lsqlite3",
                                "-Xexpect-actual-classes"
                            )
                        )
                    }
                }
            }
        }
    }

    cocoapods {  //cocoapods类似gradle管理包构建依赖，这里集成日志库
        version = "1.0"
        summary = "Some description for a Kotlin/Native module"
        homepage = "Link to a Kotlin/Native module homepage"
        ios.deploymentTarget = "14.1"
//        podfile = project.file("../ios/Podfile")
        framework {
            baseName = "LiteLibs"
            isStatic = true
        }
    }
}

android {
    namespace = "com.hwj.ai"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    }
    buildFeatures {
        buildConfig = true
    }

    kotlin {
        jvmToolchain(libs.versions.java.get().toInt())
    }
}

buildkonfig {
    packageName = "com.hwj.ai"

    defaultConfigs {
        buildConfigField(
            STRING,
            "OPENAI_API_KEY",
            "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"
        )
        buildConfigField(STRING, "DeepSeek_API_Key", "sk-d4c1947d19bb4b68ac3daac22d23985c")
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.hwj.ai.common")
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
    languageSettings.optIn("kotlin.Experimental")
    languageSettings.optIn("kotlin.RequiresOptIn")
}
