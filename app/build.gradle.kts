plugins {
    id(Plugins.androidApplication)
    id(Plugins.hilt)
    kotlin(Plugins.kotlinAndroid)
    kotlin(Plugins.kotlinExtensions)
    kotlin(Plugins.kapt)
    id(Plugins.safeArgs)
    id("kotlin-android")
}

android {
    compileSdkVersion(Configs.compileSdk)

    defaultConfig {
        applicationId = Configs.applicationId
        minSdkVersion(Configs.minSdk)
        targetSdkVersion(Configs.targetSdk)
        versionCode = Configs.versionCode
        versionName = Configs.versionName
        multiDexEnabled = true
    }

    buildFeatures {
        dataBinding = true
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
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(":domain"))
    implementation(project(":share"))
    implementation(project(":model"))
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    testImplementation(Libs.Test.junit)
    androidTestImplementation(Libs.Test.runner)
    androidTestImplementation(Libs.Test.espresso)

    // core
    implementation(Libs.Android.appCompat)
    implementation(Libs.Android.material)
    implementation(Libs.Android.recyclerView)
    implementation(Libs.Android.constraint)

    // paging
    implementation(Libs.Android.page)

    // kotlin
    implementation(Libs.Kotlin.std)

    // For dagger 2
    implementation(Libs.Dagger.core)
    kapt(Libs.Dagger.compiler)
    implementation(Libs.Dagger.viewmodel)
    kapt(Libs.Dagger.viewmodelCompiler)

    // glide
    implementation(Libs.Glide.glide1)
    kapt(Libs.Glide.glide2)
    implementation(Libs.Glide.glideTransform)

    // life cycle
    implementation(Libs.Ktx.core)
    implementation(Libs.Ktx.fragment)
    implementation(Libs.Ktx.lifeCycle)
    implementation(Libs.Ktx.liveData)
    implementation(Libs.Ktx.viewModel)
    implementation(Libs.Ktx.nav1)
    implementation(Libs.Ktx.nav2)

    // helper class
    implementation(Libs.Helper.timber)
    implementation(Libs.Helper.dialog)

    // thread
    implementation(Libs.Thread.coroutine)
    implementation(Libs.Thread.coroutineAndroid)

    // room
    implementation(Libs.Db.room)
    kapt(Libs.Db.roomCompiler)
    implementation(Libs.Db.roomRx2)
}
