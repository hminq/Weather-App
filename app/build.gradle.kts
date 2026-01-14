import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")

    kotlin("plugin.serialization") version "2.0.21"
}

// Load local.properties
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

android {
    namespace = "hminq.dev.weatherapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "hminq.dev.weatherapp"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BuildConfig fields from local.properties
        buildConfigField("String", "BASE_URL", "\"${localProperties.getProperty("base_url", "https://api.weatherapi.com/v1/")}\"")
        buildConfigField("String", "API_KEY", "\"${localProperties.getProperty("api_key", "")}\"")
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
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // Preference datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    //Hilt for DI
    implementation("com.google.dagger:hilt-android:2.57.1")
    implementation(libs.androidx.activity)
    implementation(libs.androidx.cardview)
    ksp("com.google.dagger:hilt-android-compiler:2.57.1")
    // Moshi for JSON parsing
    implementation(libs.converter.moshi)
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
    // Retrofit for API calling
    implementation(libs.retrofit2.retrofit)

    // Google Play Services Location
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Views/Fragments integration
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.navigation.ui)

    // Feature module support for Fragments
    implementation(libs.androidx.navigation.dynamic.features.fragment)

    // Testing Navigation
    androidTestImplementation(libs.androidx.navigation.testing)

    // JSON serialization library, works with the Kotlin serialization plugin
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}