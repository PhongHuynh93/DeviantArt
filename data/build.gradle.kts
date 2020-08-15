plugins {
    id(Plugins.androidLibrary)
    kotlin(Plugins.kotlinAndroid)
    kotlin(Plugins.kotlinExtensions)
    kotlin(Plugins.kapt)
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
    api(project(":model"))
    api(project(":share"))

    implementation(Libs.Kotlin.std)
    implementation(Libs.Ktx.core)

    // dagger
    implementation(Libs.Dagger.coreDagger)
    kapt(Libs.Dagger.compilerDagger)
    implementation(Libs.Dagger.core)
    implementation(Libs.Dagger.workmanager)
    kapt(Libs.Dagger.compiler)

    // network
    implementation(Libs.Network.retrofit)
    implementation(Libs.Network.gson)
    implementation(Libs.Network.log)

    // thread
    implementation(Libs.Thread.coroutine)

    // helper
    implementation(Libs.Helper.timber)
    implementation(Libs.Helper.workManager)

    // paging
    implementation(Libs.Android.page)

    // image
    implementation(Libs.Glide.glide1)
    implementation(Libs.Glide.glideTransform)
}