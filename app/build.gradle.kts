plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
//    id("com.google.devtools.ksp")
}

android {
    namespace = "com.krishnajeena.persona"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.krishnajeena.persona"
        minSdk = 29
        targetSdk = 34
        versionCode = 6
        versionName = "1.6"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/gradle/incremental.annotation.processors"
        }
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
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.media3.session)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
implementation(libs.hilt.android.compiler)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.hilt.navigation.compose)

//
    implementation("androidx.room:room-runtime:2.6.1")

    // To use Kotlin annotation processing tool (kapt)

    implementation("androidx.multidex:multidex:2.0.1")


    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.material.icons.extended.android)

    implementation(libs.androidx.room.ktx)
    implementation(libs.coil.compose)
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")  // <-- add this if missing

    implementation (libs.androidx.webkit)
    implementation (libs.material3)

    implementation (libs.accompanist.systemuicontroller)
    implementation (libs.androidx.media3.exoplayer)

    implementation (libs.androidx.media)

        implementation(libs.pdf.viewer)
    implementation (libs.photo.compose)

    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation("androidx.lifecycle:lifecycle-service:2.8.7")

    implementation("androidx.core:core:1.6.0")
    implementation("androidx.core:core-google-shortcuts:1.0.0")

    implementation ("androidx.media:media:1.7.0")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7") // or the latest version
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

}
