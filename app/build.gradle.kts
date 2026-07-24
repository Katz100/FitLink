plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization") version "2.0.21"
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.hopkins.fitlink"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hopkins.fitlink"
        minSdk = 31
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
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation ("com.polidea.rxandroidble3:rxandroidble:1.19.1")

    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    val nav_version = "2.9.8"

    // Jetpack Compose integration
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // JSON serialization library, works with the Kotlin serialization plugin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // timber
    implementation ("com.jakewharton.timber:timber:5.0.1")

    // hilt
    val hiltVersion = "2.57.1"

    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation("androidx.hilt:hilt-navigation-fragment:1.0.0")

    androidTestImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kaptTest("com.google.dagger:hilt-android-compiler:$hiltVersion")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:$hiltVersion")
    testImplementation(kotlin("test"))

    // testing
    testImplementation("com.polidea.rxandroidble3:mockclient:1.19.1")
    testImplementation (libs.junit.v412)
    testImplementation (libs.androidx.core)
    testImplementation (libs.robolectric)
    testImplementation (libs.mockclient)
    testImplementation (libs.mockito.core)
}