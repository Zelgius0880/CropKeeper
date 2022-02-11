import com.zelgius.cropkeeper.script.Versions

plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.android.gms.oss-licenses-plugin")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "com.zelgius.cropkeeper"
        minSdk = 31
        targetSdk = 32
        versionCode  =1
        versionName  ="1.0"

        testInstrumentationRunner  = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles( getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility  =JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.RequiresOptIn")
}

kapt {
    correctErrorTypes = true
}

dependencies {

    implementation( "androidx.core:core-ktx:1.7.0")
    implementation( "androidx.compose.ui:ui:${Versions.compose}")
    implementation( "androidx.compose.material:material:${Versions.compose}")
    implementation("androidx.compose.material3:material3:1.0.0-alpha05")
    implementation( "androidx.compose.ui:ui-tooling-preview:${Versions.compose}")
    implementation( "androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
    implementation( "androidx.activity:activity-compose:1.4.0")
    implementation( project(":database"))
    implementation( project(":common"))
    implementation( project(":mock"))

    //For RangeSlider. The compose range slider is more limited in customization than the Android view
    implementation ("com.google.android.material:material:1.5.0")



    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifeCycle}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifeCycle}")
    implementation("androidx.compose.runtime:runtime-livedata:${Versions.compose}")
    implementation ("androidx.compose.animation:animation:${Versions.compose}")


    //implementation("androidx.navigation:navigation-compose:2.5.0-alpha01")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    implementation ("androidx.constraintlayout:constraintlayout-compose:1.0.0")

    implementation("com.google.dagger:hilt-android:${Versions.hilt}")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.hilt}")

    implementation("org.burnoutcrew.composereorderable:reorderable:0.7.4")


    implementation( "com.google.accompanist:accompanist-navigation-animation:0.22.1-rc"){
        because("moving to 0.24.x will force compose 1.2.x-alpha and this version of compose trigger a crash in the overview screen")
    }

    implementation("com.google.android.gms:play-services-oss-licenses:17.0.0")

    testImplementation( "junit:junit:4.13.2")
    androidTestImplementation( "androidx.test.ext:junit:1.1.3")
    androidTestImplementation( "androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation( "androidx.compose.ui:ui-test-junit4:${Versions.compose}")
    debugImplementation( "androidx.compose.ui:ui-tooling:${Versions.compose}")
}