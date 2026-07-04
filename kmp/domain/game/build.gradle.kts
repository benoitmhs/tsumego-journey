plugins {
    id("config-kmp-library")
    id("setup-koin")
    alias(libs.plugins.kotest)
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Log
            implementation(libs.kermit)

            implementation(projects.kmp.core)
            implementation(projects.kmp.data)
            api(projects.kmp.domain.common)
            implementation(projects.kmp.repository)
        }
        commonTest.dependencies {
            // Unit test
            implementation(libs.kotest.engine)
            implementation(libs.kotest.assertions)
        }
    }
}
