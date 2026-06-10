@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.60.5"
}

refreshVersions {
    featureFlags {
        enable(de.fayard.refreshVersions.core.FeatureFlag.LIBS)
    }
}

include(":composeApp")
include(":app:core-ui")
include(":app:features:authentication")
include(":app:features:dashboard")
include(":app:features:maintenance")
include(":app:features:game")
include(":kmp:repository")
include(":kmp:core")
include(":kmp:data")
include(":kmp:local-datasources")
include(":kmp:remote-datasources")
include(":kmp:domain:appsettings")
include(":kmp:domain:authentication")
include(":kmp:domain:common")
include(":kmp:domain:dashboard-game")
include(":kmp:domain:profile")
include(":kmp:domain:game")
include("database-generator")

rootProject.name = "TsumegoHero"
