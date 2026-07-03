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

            // Constraint layout
            implementation(libs.constraintlayout)

            // File picking
            implementation(libs.filekit.dialogs.compose)

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            // Image async (Landscapist)
            implementation(libs.landscapist.coil)

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
            implementation(projects.kmp.core)
            implementation(projects.kmp.data)
            implementation(projects.kmp.domain.authentication)
            implementation(projects.kmp.domain.dashboardGame)
            implementation(projects.kmp.domain.game)
            implementation(projects.kmp.domain.common)
            implementation(projects.kmp.domain.rankEstimation)
        }
    }
}
