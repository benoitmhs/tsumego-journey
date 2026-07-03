# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

TsumegoHero is a Kotlin Multiplatform (Android + iOS) app, built with Compose Multiplatform, for training on Go/Baduk tsumego (life-and-death) problems. Problems are stored as SGF (Smart Game Format) files and pre-loaded into a bundled SQLite database.

## Commands

Build/run everything with the Gradle wrapper (`./gradlew`) from the repo root.

- Build everything: `./gradlew build`
- Assemble the Android debug app: `./gradlew :composeApp:assembleDebug`
- Run all tests (KMP-aggregated) for a module: `./gradlew :kmp:domain:game:allTests`
- Run just the JVM/Android unit tests for a module (faster, most common during dev): `./gradlew :kmp:domain:game:testDebugUnitTest`
- Run a single test class: `./gradlew :kmp:domain:game:testDebugUnitTest --tests "sgfparsing.SgfParserTest"`
- Run iOS native tests for a module: `./gradlew :kmp:domain:game:iosSimulatorArm64Test`
- Static analysis (Detekt, config at [detekt-config.yml](detekt-config.yml)): `./gradlew detekt` (whole project) or `./gradlew :module:path:detekt`
- Android lint: `./gradlew :composeApp:lintDebug`

Tests live in `src/commonTest` per module and use Kotest (`FunSpec` style, `shouldBe` matchers) — see [kmp/domain/game/src/commonTest/kotlin/sgfparsing/SgfParserTest.kt](kmp/domain/game/src/commonTest/kotlin/sgfparsing/SgfParserTest.kt) as the pattern to follow. Most modules currently have no tests; don't assume coverage exists elsewhere.

Detekt notes: `warningsAsErrors: true` in the config, but the Gradle plugin wrapper sets `ignoreFailures = true` ([build-logic/src/main/kotlin/plugins/setup-detekt.gradle.kts](build-logic/src/main/kotlin/plugins/setup-detekt.gradle.kts)), so Detekt issues won't fail the build — check output manually. All `formatting:` rules are disabled (commented out) and `MagicNumber`/`ForbiddenComment` are off, so don't flag those in review. `LongMethod` (250), `CyclomaticComplexMethod` (35) and `LargeClass` (1000) thresholds are relaxed well above Detekt defaults.

There is no CI configuration in this repo (no `.github/workflows`) — Gradle commands above are the only verification available.

## Architecture

This is a multi-module Gradle project using custom convention plugins (`build-logic/`) rather than repeating boilerplate per module. Every `kmp/*` and `app/*` library module applies `id("config-kmp-library")` (or `config-kmp-app` for the app module), which wires up Android SDK versions, KMP targets (Android + iOS x64/arm64/simulatorArm64), and Detekt via [build-logic/src/main/kotlin/plugins](build-logic/src/main/kotlin/plugins). Modules needing DI additionally apply `id("setup-koin")`, which configures KSP for Koin's annotation processor across the `commonMain` metadata source set.

Dependency injection is Koin throughout (no Hilt/Dagger). Each layer module exposes a Koin `Module` (usually in a `di/` package) that's aggregated up into `composeApp`.

### Module layering (clean-architecture style, no explicit interfaces — DI provides the seams)

- `kmp/core` — lowest-level shared utilities, no business logic.
- `kmp/data` — pure data models/DTOs (e.g. `RawTsumego`, `Rank`, `AppConfig`), kotlinx.serialization, no logic.
- `kmp/local-datasources` — Room database (`AppDatabase`, package `com.mrsanglier.tsumegohero.localdatasources.room`), DAOs (`TsumegoDao`, `ProfileDao`), and datasource wrappers that map Room entities to `kmp/data` models. Despite the `schemas/` directory name (a Room schema-export convention, not SQLDelight), this is AndroidX Room used in KMP mode with bundled SQLite.
- `kmp/remote-datasources` — placeholder module for future networking (Ktor is already a dependency project-wide but this module's Koin module is currently empty).
- `kmp/repository` — thin facade layer (e.g. `TsumegoRepository`) combining local/remote datasources behind a single API consumed by domain code.
- `kmp/domain/*` (`appsettings`, `authentication`, `common`, `dashboard-game`, `game`, `profile`) — one module per bounded business concern. Contains UseCases and Delegates registered as Koin singletons; this is where game/business rules live (e.g. SGF parsing and board-state derivation in `kmp/domain/game`).
- `app/core-ui` — shared Compose Multiplatform component library (buttons, dialogs, snackbars, navigation bar, theme) plus cross-cutting singletons like `SnackbarManager`, `AlertDialogManager`, `LoadingManager` that features trigger via Koin injection rather than direct references.
- `app/features/*` (`authentication`, `dashboard`, `game`, `maintenance`) — one Compose feature per screen area, MVVM pattern: a `ViewModel` (androidx.lifecycle) exposing an `@Immutable` state data class via `StateFlow` (`MutableStateFlow` → `.stateIn(WhileSubscribed)`), with UseCases injected via Koin. See `GameViewModel`/`GameViewModelState` in `app/features/game` as the reference implementation.
- `composeApp` — the app shell. Wires every module together, owns top-level navigation. `App.kt` observes a `MainGraph` sealed state (`ForceUpdate` / `Maintenance` / `Authentication` / `Main`) from `AppViewModel` and `Crossfade`s between the corresponding top-level screens/navigation graphs.
- `iosApp` — SwiftUI entry point required by KMP/Compose Multiplatform for iOS; hosts the shared `composeApp` framework.

### Data flow example (typical read path)

`app/features/*` ViewModel → UseCase (`kmp/domain/*`) → `TsumegoRepository` (`kmp/repository`) → `LocalTsumegoDatasource` (`kmp/local-datasources`, Room) → mapped back up through `kmp/data` models.

### Tsumego database generation

`database-generator/` is a standalone JVM CLI tool (`com.example.database_generator.main.kt`), **not** part of the app build graph. It walks the top-level [sgf/](sgf) directory for `.sgf` files, parses each into a `RawTsumego` (extracting rank from filename/content), and batch-inserts (1000 rows/batch) into a generated `build/tsumego.db` SQLite file matching the Room schema in `kmp/local-datasources`. This generated DB is what gets bundled/prepopulated into the app (see recent commits "prepopulate ios database", "count tsumego database", "generate initial database"). If you add/modify SGF problem files under `sgf/`, re-run this tool to regenerate the bundled database.

### Config

- `BuildKonfig` provides compile-time environment config (`BuildKonfig.ENVIRONMENT`, flavor set via `buildkonfig.flavor` in [gradle.properties](gradle.properties)), read in `composeApp`'s `AppConfigProvider`.
- SDK versions (minSdk 24, targetSdk/compileSdk 36) and app ID are centralized in `build-logic/src/main/kotlin/config/ConfigurationKeys.kt` — change them there, not per-module.
