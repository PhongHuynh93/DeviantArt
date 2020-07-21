plugins {
    id(Plugins.androidLibrary)
    kotlin(Plugins.kotlinAndroid)
    kotlin(Plugins.kotlinExtensions)
    kotlin(Plugins.kapt)
    id(Plugins.hilt)
    id("kotlin-android")
}

android {
    compileSdkVersion(Configs.compileSdk)

    defaultConfig {
        minSdkVersion(Configs.minSdk)
        targetSdkVersion(Configs.targetSdk)
        versionCode = Configs.versionCode
        versionName = Configs.versionName
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    api(project(":data"))
    implementation(Libs.Kotlin.std)
    implementation(Libs.Ktx.core)

    // dagger
    implementation(Libs.Dagger.core)
    kapt(Libs.Dagger.compiler)

    // thread
    implementation(Libs.Thread.coroutine)
    implementation(Libs.Thread.coroutineAndroid)

    // helper
    implementation(Libs.Helper.timber)
}