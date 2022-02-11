import  com.zelgius.cropkeeper.script.BuildDbTask
import  com.zelgius.cropkeeper.script.Versions

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 31
        targetSdk = 32

        buildConfigField("int", "DATABASE_VERSION", "${Versions.databaseVersion}")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.register("create_prepopulated_db",BuildDbTask::class)

kapt {
    correctErrorTypes = true
}

dependencies {

    implementation("androidx.core:core-ktx:1.7.0")
    implementation( project(":common"))

    implementation("androidx.room:room-runtime:${Versions.room}")

    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:${Versions.room}")
    api("androidx.room:room-ktx:${Versions.room}")

    api("androidx.room:room-paging:2.4.1")

    implementation("com.google.dagger:hilt-android:${Versions.hilt}")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.hilt}")
    
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.room:room-testing:${Versions.room}")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

}