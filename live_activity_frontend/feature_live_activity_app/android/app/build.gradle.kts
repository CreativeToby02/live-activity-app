plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" // <-- Add this line
    id("dev.flutter.flutter-gradle-plugin")
}
android {
    namespace = "com.example.feature_live_activity_app"
    compileSdk = 35
    ndkVersion = flutter.ndkVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    defaultConfig {
        applicationId = "com.example.feature_live_activity_app"
        minSdk = flutter.minSdkVersion
        targetSdk = 34
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }

    buildTypes {
        release {
            // TODO: Add your own signing config for the release build.
            // Signing with the debug keys for now, so `flutter run --release` works.
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    // ✅ Compose configuration
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

flutter {
    source = "../.."
}
// build.gradle.kts (Module: app)

dependencies {
    // Import the Compose BOM to manage Compose library versions
    // Always use the latest stable version
    // Check https://developer.android.com/jetpack/compose/bom
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00") // Keep one declaration
    implementation(composeBom) // Apply it once

    // Core Compose UI dependencies
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Material Design 3 (for LinearProgressIndicator, MaterialTheme, Text)
    implementation("androidx.compose.material3:material3")

    // Also ensure you have activity-compose for setContent
    implementation("androidx.activity:activity-compose:1.10.1") // Keep the latest version

    // This dependency provides ViewTreeLifecycleOwner. Keep the latest version (2.9.2)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2") // Single, latest declaration

    // For NotificationCompat (if you're using it in LiveNotificationManager)
    // Prefer core-ktx as it includes core functionality with Kotlin extensions
    implementation("androidx.core:core-ktx:1.16.0") // Keep core-ktx
    // You DO NOT need both core-ktx and core. Remove the 'core' line.
    // implementation("androidx.core:core:1.13.1") // REMOVE THIS LINE

    // If you explicitly intended to use core-notify alpha, uncomment it,
    // otherwise core-ktx usually suffices for basic notification builder usage.
    // implementation("androidx.core:core-notify:1.0.0-alpha01")
}