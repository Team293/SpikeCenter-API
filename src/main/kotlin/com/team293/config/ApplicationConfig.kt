package com.team293.config

import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithName

@ConfigMapping(prefix = "application")
interface ApplicationConfig {
    @WithName("auth-backend-base-url")
    fun authBackendBaseUrl(): String
}