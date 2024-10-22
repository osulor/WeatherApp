plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.weatherappcodingchallenge"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weatherappcodingchallenge"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {


    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Retrofit for Network Calls
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Gson for JSON Parsing
    implementation(libs.gson)

    // OkHttp (optional) for logging HTTP requests
    implementation(libs.logging.interceptor)

    // Kotlin and AndroidX Libraries
    implementation(libs.kotlin.stdlib)


    // Lifecycle and ViewModel for MVVM
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Glide for Image Loading
    implementation(libs.glide)
    //ksp(libs.compiler)

    implementation(libs.play.services.location)

    // Mockito for mocking objects
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)

    // Coroutines testing
    testImplementation(libs.kotlinx.coroutines.test)

    // Android LiveData testing
    testImplementation (libs.androidx.core.testing)


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}