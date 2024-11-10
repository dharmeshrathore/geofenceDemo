plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("kotlin-kapt")
    alias(libs.plugins.maps.platform.gradle.plugin)
    alias(libs.plugins.google.service.plugin)
    alias(libs.plugins.firebase.crashlytics.plugin)
}

android {
    namespace = "com.dharmesh.geofencedemo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dharmesh.geofencedemo"
        minSdk = 24
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "api_key", project.findProperty("api_key") as String)
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
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.frament.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.feature.fragment)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Lifecycles only (without ViewModel or LiveData)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // coroutine
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.test)

    //Google Play Services
    implementation(libs.play.service.location)
    implementation(libs.play.service.maps)

    // Koin.io for dependency injection
    implementation(libs.io.insert.koin)
    implementation(libs.io.insert.koin.android.compat)

    //firebase
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
}