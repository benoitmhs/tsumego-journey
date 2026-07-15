import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("config-kmp-app")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.updateLoco)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildKonfig)
}

kotlin {
    sourceSets {

        androidMain.dependencies {
            implementation(libs.compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            // AndroidX
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            // Compose
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.preview)

            // Koin
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            // Kotlin
            implementation(libs.kotlinx.serialization.json)

            // Logger
            implementation(libs.kermit)

            // Navigation
            implementation(libs.navigation.compose)
            implementation(libs.androidx.lifecycle.viewmodel)

            // Project modules
            implementation(projects.app.coreUi)
            implementation(projects.app.features.authentication)
            implementation(projects.app.features.dashboard)
            implementation(projects.app.features.game)
            implementation(projects.app.features.maintenance)
            implementation(projects.kmp.core)
            implementation(projects.kmp.data)
            implementation(projects.kmp.localDatasources)
            implementation(projects.kmp.remoteDatasources)
            implementation(projects.kmp.repository)
            implementation(projects.kmp.domain.appsettings)
            implementation(projects.kmp.domain.authentication)
            implementation(projects.kmp.domain.common)
            implementation(projects.kmp.domain.dashboardGame)
            implementation(projects.kmp.domain.profile)
            implementation(projects.kmp.domain.game)
            implementation(projects.kmp.domain.rankEstimation)
        }
    }
}

// Same flavor property as BuildKonfig (buildkonfig.flavor in gradle.properties, dev by default)
val buildkonfigFlavor: String = project.findProperty("buildkonfig.flavor") as? String ?: "dev"

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        // Distinct applicationId per flavor: dev installs alongside prod. The namespace
        // (Kotlin packages, generated R) stays the same on purpose.
        if (buildkonfigFlavor != "prod") {
            applicationId = "$applicationId.${buildkonfigFlavor}"
        }
    }

    dependencies {
        debugImplementation(libs.compose.preview)
    }
}

// Download strings script
val properties = Properties()
project.rootProject.file("local.properties").inputStream().use { properties.load(it) }

buildkonfig {
    packageName = "com.mrsanglier.tsumegohero"
    defaultConfigs {
        buildConfigField(STRING, "ENVIRONMENT", "dev")
    }
    defaultConfigs("dev") {
        buildConfigField(STRING, "ENVIRONMENT", "dev")
    }
    defaultConfigs("prod") {
        buildConfigField(STRING, "ENVIRONMENT", "prod")
    }
}

Loco {
    config {
        apiKey = properties.getProperty("LOCO_AFO_KEY") ?: throw Exception("no loco api key")
        lang = arrayOf("en", "fr")
        resDir = "${project.projectDir.path}/../app/core-ui/src/commonMain/composeResources"
        defLang = "en"
        fileName = "strings"
        hideComments = true
        orderByAssetId = true
    }
}
