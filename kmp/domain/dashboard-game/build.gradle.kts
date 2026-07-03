plugins {
    id("config-kmp-library")
    id("setup-koin")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Log
            implementation(libs.kermit)

            implementation(projects.kmp.core)
            implementation(projects.kmp.data)
            implementation(projects.kmp.domain.common)
            implementation(projects.kmp.repository)
        }
    }
}
