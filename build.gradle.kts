plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinCocoapods) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.buildKonfig) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.jvm) apply false
//    alias(libs.plugins.kotlinKapt) apply false
}


buildscript {
    dependencies {
        classpath(libs.buildkonfig.gradle.plugin)
//        classpath(libs.objects.plugin)
    }
}