plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.2.10"
    id("org.jetbrains.kotlin.plugin.allopen") version "2.2.10"
}

dependencies {
    implementation(project(":common"))
    compileOnly("jakarta.enterprise:jakarta.enterprise.cdi-api:4.1.0")
}
