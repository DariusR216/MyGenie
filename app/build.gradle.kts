plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.flightpath.mygenie"
    compileSdk = 35

    buildFeatures {
        buildConfig = true // Enable BuildConfig generation
    }

    defaultConfig {
        applicationId = "com.flightpath.mygenie"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Use Kotlin DSL syntax for buildConfigField
        buildConfigField("String", "OPENAI_API_KEY", "\"${project.findProperty("OPENAI_API_KEY") ?: ""}\"")
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
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.8.1")) // Use latest BOM version
    implementation("com.google.firebase:firebase-analytics") // Example: Firebase Analytics
    implementation("com.google.firebase:firebase-auth") // No version needed
    implementation("com.google.firebase:firebase-firestore") // No version needed
    implementation("com.google.firebase:firebase-messaging") // No version needed
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // OkHttp dependency
}
