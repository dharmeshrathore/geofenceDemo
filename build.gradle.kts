// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.maps.platform.gradle.plugin) apply false
    alias(libs.plugins.firebase.crashlytics.plugin) apply false
    alias(libs.plugins.google.service.plugin) apply false
}


tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}