import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.serialization") version "2.2.10"
    id("org.jetbrains.kotlin.plugin.allopen") version "2.2.10"
}

val quarkusPlatformVersion = "3.26.3"

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "com.myorg.scouting"
    version = "0.1.0"

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    // Align all deps with the Quarkus platform BOM
    dependencies {
        // enforcedPlatform keeps versions consistent across modules
        add("implementation", platform("io.quarkus.platform:quarkus-bom:$quarkusPlatformVersion"))
        add("testImplementation", platform("io.quarkus.platform:quarkus-bom:$quarkusPlatformVersion"))

        add("testImplementation", "io.quarkus:quarkus-junit5")
        add("testImplementation", "io.rest-assured:rest-assured")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.add("-Xjsr305=strict")
        }
    }
}
