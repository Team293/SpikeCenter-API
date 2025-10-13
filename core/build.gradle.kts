plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.2.10"
    id("org.jetbrains.kotlin.plugin.allopen") version "2.2.10"
    id("org.jetbrains.kotlin.plugin.noarg") version "2.2.10"
}

dependencies {
    implementation(project(":common"))

    compileOnly("io.quarkus:quarkus-hibernate-orm-panache-kotlin")
    compileOnly("jakarta.persistence:jakarta.persistence-api:3.1.0")
    compileOnly("jakarta.enterprise:jakarta.enterprise.cdi-api:4.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

noArg {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
    invokeInitializers = true
}
