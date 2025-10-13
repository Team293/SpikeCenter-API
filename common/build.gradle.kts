plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.2.10"
    id("org.jetbrains.kotlin.plugin.allopen") version "2.2.10"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    compileOnly("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")
}
