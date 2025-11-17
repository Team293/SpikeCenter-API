plugins {
    id("io.quarkus") version "3.26.3"
    kotlin("plugin.serialization") version "2.2.10"
    id("org.jetbrains.kotlin.plugin.allopen") version "2.2.10"
    kotlin("jvm")
}

dependencies {
    // internal modules
    implementation(project(":common"))
    implementation(project(":core"))
    implementation(project(":api"))
    implementation(project(":analytics"))

    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-arc")

    implementation("io.quarkus:quarkus-rest-kotlin-serialization")

    implementation("io.quarkus:quarkus-hibernate-orm")
    implementation("io.quarkus:quarkus-hibernate-orm-panache-kotlin")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkus:quarkus-flyway")

    implementation("io.quarkus:quarkus-config-yaml")
    implementation("io.quarkus:quarkus-logging-json")
    implementation("io.quarkus:quarkus-smallrye-health")
    implementation("io.quarkus:quarkus-micrometer")
    implementation("io.quarkus:quarkus-micrometer-registry-prometheus")
    implementation("io.quarkus:quarkus-opentelemetry")
    implementation("io.quarkus:quarkus-cache")
    implementation("io.quarkus:quarkus-scheduler")
    implementation("io.quarkus:quarkus-websockets-next")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.quarkus:quarkus-oidc")
    implementation("io.quarkus:quarkus-keycloak-authorization")
    implementation("io.quarkus:quarkus-keycloak-admin-resteasy-client")
}
