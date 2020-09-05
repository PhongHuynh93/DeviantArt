plugins {
  id(Plugins.androidLibrary)
  kotlin(Plugins.kotlinAndroid)
  kotlin(Plugins.kotlinExtensions)
  kotlin(Plugins.kapt)
}

android {
  compileSdkVersion(Configs.compileSdk)
  resourcePrefix("irv_")

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
  implementation(Libs.Android.appCompat)
  implementation(Libs.Kotlin.std)
  implementation(Libs.Ktx.core)
  implementation(Libs.Android.recyclerView)
}