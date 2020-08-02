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

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        this as org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
        jvmTarget = "1.8"
    }
}

dependencies {
    // kotlin
    implementation(Libs.Kotlin.std)

    // android lib
    implementation(Libs.Android.appCompat)
    implementation(Libs.Android.material)

    // kts
    implementation(Libs.Ktx.core)
    implementation(Libs.Ktx.fragment)
    implementation(Libs.Ktx.lifeCycle)
    implementation(Libs.Ktx.liveData)
    implementation(Libs.Ktx.viewModel)
    implementation(Libs.Ktx.nav1)
    implementation(Libs.Ktx.nav2)

    // glide
    implementation(Libs.Glide.glide1)
    kapt(Libs.Glide.glide2)
}