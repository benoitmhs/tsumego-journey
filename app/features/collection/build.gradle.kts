plugins {
    id("config-kmp-library")
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.preview)
        }
        commonMain.dependencies {
            // Compose
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.preview)

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            // File picking
            implementation(libs.filekit.dialogs.compose)

            // Haze
            implementation(libs.haze)

            // Log
            implementation(libs.kermit)

            // Navigation
            implementation(libs.navigation.compose)
            implementation(libs.androidx.lifecycle.viewmodel)

            // Serialization
            implementation(libs.kotlinx.serialization.json)

            // Project
            implementation(projects.app.coreUi)
            implementation(projects.app.features.game)
            implementation(projects.kmp.core)
            implementation(projects.kmp.data)
            implementation(projects.kmp.domain.collection)
            implementation(projects.kmp.domain.game)
        }
    }
}
