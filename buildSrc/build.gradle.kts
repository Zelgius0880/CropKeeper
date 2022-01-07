plugins {
    `kotlin-dsl`
    //id("kotlinx-serialization")
    kotlin("plugin.serialization") version "1.6.10"
}
dependencies {
    implementation(kotlin("serialization"))
    implementation(group = "org.xerial", name = "sqlite-jdbc", version = "3.36.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
}

repositories {
    mavenCentral()
    google()
}