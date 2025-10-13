plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.2.10"
    id("org.jetbrains.kotlin.plugin.allopen") version "2.2.10"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":common"))

    compileOnly("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")
    compileOnly("jakarta.enterprise:jakarta.enterprise.cdi-api:4.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-security")
    implementation("io.quarkus:quarkus-oidc")
    implementation("io.quarkus:quarkus-keycloak-authorization")
}
