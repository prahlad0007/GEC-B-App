plugins {
    id("com.android.application") version "8.10.0"
    id("org.jetbrains.kotlin.android") version "2.0.21"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
    id("com.google.gms.google-services") // ✅ Google services plugin added
}

android {
    namespace = "com.example.gecbadminapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gecbadminapp"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // ✅ Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.android)

    // ✅ Compose Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ✅ Firebase BoM (from your request, updated to latest)
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-auth")

    // ✅ Hilt
    implementation("com.google.dagger:hilt-android:2.50")
//    kapt("com.google.dagger:hilt-compiler:2.50") // Uncomment if needed
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // ✅ Coil (image loading)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // ✅ Optional: Accompanist WebView
    implementation("com.google.accompanist:accompanist-webview:0.34.0")

    // ✅ Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

//    implementation ("com.github.barteksc:android-pdf-viewer:3.2.0-beta.1")
    // ✅ for Cloudinary-->
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // or latest
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("com.cloudinary:cloudinary-android:3.0.2")

    implementation ("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")



}
