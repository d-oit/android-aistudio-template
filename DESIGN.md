---
name: Android AI Studio Template
description: >
  Offline-first Android application template with optional Gemini AI cloud
  enrichment. Jetpack Compose + Material 3 UI, MVVM architecture, Room
  persistence, WorkManager background sync, and Retrofit networking.
version: "1.0.0"
status: active
authors:
  - d-oit
repository: https://github.com/d-oit/android-aistudio-template
license: MIT
last_updated: "2026-07-21"
---

# Design Specification — Android AI Studio Template

> This document is the machine-readable design specification for the project.
> AI coding agents (Gemini in AI Studio, Copilot, Claude, Codex) **must** read
> this file before making structural changes, adding features, or modifying
> the data model. See also: `AGENTS.md`, `SPEC.md`, `TASK.md`.

---

## 1. Project Overview

### 1.1 Purpose

A production-ready Android template that demonstrates offline-first
architecture with optional AI cloud enrichment via Google Gemini. The template
is designed for AI coding agents to fork, extend, and ship features with
minimal human intervention.

### 1.2 Target Audience

- Android developers starting new projects
- AI coding agents building Android apps in Google AI Studio
- Teams evaluating offline-first + AI-enrichment patterns

### 1.3 Key Capabilities

| Capability | Status |
|---|---|
| Jetpack Compose UI (Material 3) | ✅ Implemented |
| Room local database | ✅ Scaffold |
| Retrofit + OkHttp networking | ✅ Scaffold |
| WorkManager background sync | ✅ Scaffold |
| Gemini AI integration | ✅ Scaffold |
| On-device AI fallback | 🚧 Planned (Phase 2) |
| Signed release builds | 🚧 Planned (Phase 4) |

---

## 2. Architecture

### 2.1 Layered Architecture (MVVM + Repository)

```
┌──────────────────────────────────────────────────────┐
│  UI Layer                                            │
│  Compose Screens ← ViewModel (StateFlow<UiState>)   │
├──────────────────────────────────────────────────────┤
│  Domain Layer                                        │
│  UseCase classes (business logic, prompt assembly)   │
├──────────────────────────────────────────────────────┤
│  Data Layer                                          │
│  Repository (orchestrates local + remote sources)    │
├──────────────────────┬───────────────────────────────┤
│  Local Sources       │  Remote Sources               │
│  Room DAOs + Entities│  Retrofit API Services        │
│                      │  Gemini AI Client             │
├──────────────────────┴───────────────────────────────┤
│  Background Layer                                    │
│  WorkManager SyncWorkers (reactive, not UI-driven)   │
└──────────────────────────────────────────────────────┘
```

### 2.2 Layer Responsibilities

| Layer | Classes | Responsibility |
|---|---|---|
| UI | `*Screen`, `*ViewModel` | Render state, emit user actions |
| Domain | `*UseCase` | Business logic, AI prompt assembly |
| Data | `*Repository` | Orchestrate local + remote, sync state |
| Local | `*Dao`, `*Entity` | Room persistence |
| Remote | `*ApiService` | Retrofit REST clients |
| AI | `AiRepository`, `GeminiClient` | Gemini cloud + on-device fallback |
| Sync | `*SyncWorker` | WorkManager background sync |

### 2.3 Data Flow

```
User Action → Composable Screen
  → ViewModel.action(intent)
    → UseCase.execute(params)
      → Repository.get/update(...)
        → [Room DAO] ← local read/write
        → [ApiService] ← remote fetch (optional)
        → [AiRepository] ← AI enrichment (optional)
      → SyncState flag set (PENDING_SYNC)
    → StateFlow<UiState> emitted
  → Screen recomposes
```

### 2.4 Design Decisions (ADRs)

#### ADR-001: Manual Constructor Injection (No Hilt)

- **Context**: Reducing framework complexity for a template; DI containers add compile-time overhead.
- **Decision**: Use manual constructor injection. All dependencies instantiated in `Application` class and passed explicitly.
- **Consequences**: More boilerplate, but zero DI framework coupling. Every class is testable by passing mocks to constructors.
- **Constraint**: Never use `@Inject`, `@HiltViewModel`, `@AndroidEntryPoint`, or `@HiltAndroidApp`.

#### ADR-002: System Gradle for CI, Wrapper for Local

- **Context**: The `gradle-wrapper.jar` was historically corrupt (downloaded as text, not binary).
- **Decision**: CI uses system-installed `gradle` command via `harness.sh`. The wrapper jar is committed for local developer use.
- **Consequences**: CI is resilient to wrapper corruption. Local developers use `./gradlew` normally.

#### ADR-003: ktfmt Google Style over ktlint

- **Context**: ktlint had trailing-comma and indent formatting inconsistencies.
- **Decision**: Switched to ktfmt with `.googleStyle()` via Spotless.
- **Consequences**: Consistent 2-space indentation, no trailing comma debates. Spotless handles auto-formatting.

#### ADR-004: Reusable CI Workflow

- **Context**: Single monolithic CI job was hard to maintain.
- **Decision**: Split into `ci.yml` (trigger) + `_build.yml` (reusable workflow). Single `harness.sh verify` gate.
- **Consequences**: DRY CI configuration. Easy to add new trigger workflows.

#### ADR-005: No Instrumented Tests

- **Context**: `androidTest/` requires emulator/device, slow CI, flaky.
- **Decision**: All tests run on JVM via Robolectric + Roborazzi. No `androidTest/` directory.
- **Consequences**: Fast CI (~2min test suite). Screenshot testing via Roborazzi on JVM.

---

## 3. Data Model

### 3.1 Current Entities

| Entity | Table | Key Fields | Sync State |
|---|---|---|---|
| *(Scaffold stage — no entities defined yet)* | — | — | — |

### 3.2 Planned Entities (Phase 1–2)

| Entity | Table | Key Fields | Sync State |
|---|---|---|---|
| `Note` | `notes` | id, title, content, createdAt, updatedAt | syncStatus, isDeleted |
| `AiResult` | `ai_results` | id, sourceId, resultType, content, confidence | syncStatus |

### 3.3 Sync State Machine

Every mutable entity must include a `syncStatus` field following this state machine:

```
[Local Change]
    │
    ▼
PENDING_SYNC ──── SyncWorker picks up ────► IN_PROGRESS
    │                                            │
    │                                     ┌──────┴──────┐
    │                                     ▼             ▼
    │                                  SYNCED        FAILED
    │                                     │             │
    │                                     │        Retry (exponential backoff)
    │                                     ▼             │
    │                                  [No-op]     Max retries ──► CONFLICT
    │                                                      │
    │                                                 Manual resolution
    ▼
[Soft-delete sets isDeleted=true, syncStatus=PENDING_SYNC]
```

### 3.4 Room Configuration

```kotlin
@Database(
    entities = [/* Add entities here */],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    // abstract fun noteDao(): NoteDao
}
```

---

## 4. API & Interface Design

### 4.1 Retrofit Services

| Service | Base URL | Purpose |
|---|---|---|
| `GeminiApiService` | `https://generativelanguage.googleapis.com/` | Gemini AI content generation |

### 4.2 Gemini API Integration

```kotlin
interface GeminiApiService {
    @POST("models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}
// Note: v1beta/ is in GeminiConfig.BASE_URL, not in the annotation
```

### 4.3 AI State Machine

```
User triggers AI action
    │
    ▼
Is GEMINI_API_KEY set?
    ├─ No  → OnDeviceFallback → AiResult.OnDevice
    └─ Yes → GeminiApiService.generateContent()
                 │
                 ├─ Success → AiResult.Cloud
                 └─ Exception → OnDeviceFallback → AiResult.OnDevice
```

---

## 5. State Management

### 5.1 ViewModel Pattern

```kotlin
class ExampleViewModel(
    private val repository: ExampleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ExampleUiState>(ExampleUiState.Loading)
    val uiState: StateFlow<ExampleUiState> = _uiState.asStateFlow()

    init { loadData() }

    private fun loadData() {
        viewModelScope.launch {
            repository.getDataFlow()
                .catch { _uiState.value = ExampleUiState.Error(it.message) }
                .collect { _uiState.value = ExampleUiState.Success(it) }
        }
    }
}
```

### 5.2 UiState Sealed Interface

```kotlin
sealed interface ExampleUiState {
    data object Loading : ExampleUiState
    data class Success(val data: List<Item>) : ExampleUiState
    data class Error(val message: String?) : ExampleUiState
}
```

### 5.3 State Rules

1. **Screens are stateless** — all state lives in ViewModel as `StateFlow`.
2. **State hoisting** — Composables receive state + callbacks, never hold state.
3. **Single source of truth** — Room DB is authoritative; ViewModel exposes derived state.
4. **No mutable state in Composables** — use `remember` only for UI-only state (scroll position).

---

## 6. Security Model

### 6.1 Secrets Injection

| Secret | Storage | Injection |
|---|---|---|
| `GEMINI_API_KEY` | `.env` file / CI secret | `BuildConfig.GEMINI_API_KEY` |
| `GH_TOKEN` | Shell env / CI secret | `BuildConfig.GH_TOKEN` |
| User PATs | `EncryptedSharedPreferences` | Android Keystore `MasterKey` |

### 6.2 Security Rules

1. **Never commit `.env`** or any file containing API keys.
2. **Never hardcode secrets** in source files.
3. **BuildConfig injection only** — values read at build time from environment.
4. **Encrypted storage** for user-facing credentials.
5. **HTTPS only** for all network traffic.

---

## 7. Testing Strategy

### 7.1 Test Pyramid

| Layer | Framework | Location | Speed |
|---|---|---|---|
| Unit | JUnit 4 + Mockito | `app/src/test/` | <1s per test |
| Integration | Robolectric | `app/src/test/` | 1-5s per test |
| Screenshot | Roborazzi | `app/src/test/` | 2-10s per test |
| Coverage | JaCoCo | CI + local | N/A |

### 7.2 Testing Rules

1. **No instrumented tests** — never create `androidTest/` files.
2. **Robolectric for all Android-dependent tests** — runs on JVM, no emulator.
3. **Test naming**: `fun should_doX_when_conditionY()`.
4. **Coroutine tests**: use `runTest {}` for suspend functions.
5. **Screenshot baselines**: commit Roborazzi reference images; CI compares.
6. **Minimum coverage**: 70% line coverage (JaCoCo in CI).

### 7.3 Test File Convention

```
src/test/java/com/example/template/
  ├── ai/
  │   └── AiRepositoryTest.kt
  ├── data/
  │   └── NoteRepositoryTest.kt
  ├── ui/
  │   ├── HomeScreenTest.kt
  │   └── screenshots/
  │       └── HomeScreenScreenshotTest.kt
  └── viewmodel/
      └── HomeViewModelTest.kt
```

---

## 8. CI/CD Pipeline

### 8.1 Workflow Architecture

```
ci.yml (trigger)
  → _build.yml (reusable workflow)
    → Checkout → JDK 21 (Zulu) → Android SDK (setup-android)
    → Auto-fix: ./harness.sh format (spotlessApply)
    → Quality Gate: ./harness.sh verify
      → format-check (spotlessCheck)
      → lint (lintDebug)
      → test (testDebugUnitTest)
      → build (assembleDebug)
      → coverage (jacocoTestReportDebug)
    → Upload APK artifact
    → Upload coverage to Codacy (if token present)
```

### 8.2 Workflows

| Workflow | Trigger | Purpose |
|---|---|---|
| `ci.yml` | push to main/develop, PR to main | Quality gate |
| `_build.yml` | workflow_call (reusable) | Build + verify steps |
| `release.yml` | push tag `v*` | Signed release APK + GitHub Release |
| `dependabot-auto-merge.yml` | PR (dependabot) | Auto-approve + merge patch updates |

### 8.3 Harness Commands

All development tasks go through `./harness.sh`:

| Command | Purpose |
|---|---|
| `help` | Show usage guide |
| `verify` | Full gate: format → lint → test → build → coverage |
| `check` | Static gate: format → lint → test |
| `build` | Build debug APK |
| `test` | Run full test suite |
| `lint` | Android Lint |
| `format` | Apply Spotless formatting |
| `format-check` | Check Spotless formatting |
| `detekt` | Static analysis |
| `coverage` | JaCoCo report |

### 8.4 Quality Gate Definition of Done

Before declaring any change complete:

1. `./harness.sh format` — auto-fix formatting
2. `./harness.sh check` — static analysis + tests
3. `./harness.sh build` — verify APK builds
4. `git commit && git push` — push to PR branch
5. All CI checks pass on GitHub (`gh pr checks`)
6. Screenshot baselines updated if Compose UI changed

---

## 9. Development Workflow

### 9.1 Adding a New Feature

```
1. Read SPEC.md for architecture context
2. Read TASK.md for current phase and pending items
3. Create UseCase class for domain logic
4. Create/update Repository with constructor injection
5. Create ViewModel with StateFlow<UiState>
6. Create Compose Screen (stateless, hoisted state)
7. Add Room Entity + DAO if persistence needed
8. Add Retrofit service if remote API needed
9. Add WorkManager Worker if background sync needed
10. Write unit tests for Repository + ViewModel
11. Write Compose UI tests for Screen
12. Run ./harness.sh verify
13. Update TASK.md with completed items
```

### 9.2 File Naming Conventions

| Type | Convention | Example |
|---|---|---|
| Screen | `*Screen.kt` | `HomeScreen.kt` |
| ViewModel | `*ViewModel.kt` | `HomeViewModel.kt` |
| UseCase | `*UseCase.kt` | `SummarizeTextUseCase.kt` |
| Repository | `*Repository.kt` | `NoteRepository.kt` |
| Entity | `*Entity.kt` | `NoteEntity.kt` |
| DAO | `*Dao.kt` | `NoteDao.kt` |
| API Service | `*ApiService.kt` | `GeminiApiService.kt` |
| Worker | `*SyncWorker.kt` | `NoteSyncWorker.kt` |
| Test | `*Test.kt` | `NoteRepositoryTest.kt` |

### 9.3 Package Structure

> **Note**: Packages below marked with `(planned)` do not exist yet. Only `ai/` and `ui/theme/` are implemented.

```
com.example.template/
  ├── ai/                    # Gemini AI integration ✅
  ├── data/                  # (planned) Repositories, data sources
  ├── db/                    # (planned) Room entities, DAOs, database
  ├── domain/                # (planned) Use cases
  ├── network/               # (planned) Retrofit services
  ├── sync/                  # (planned) WorkManager workers
  ├── ui/                    # Compose screens + theme ✅
  │   ├── home/              # (planned)
  │   └── theme/
  └── MainActivity.kt
```

---

## 10. Dependency Map

### 10.1 Core Dependencies

| Category | Library | Version | Purpose |
|---|---|---|---|
| Language | Kotlin | 2.2.10 | Primary language |
| Build | AGP | 9.3.0 | Android Gradle Plugin |
| UI | Compose BOM | 2025.05.00 | Jetpack Compose |
| UI | Material 3 | 1.3.2 | Material Design |
| Persistence | Room | 2.7.0 | Local database |
| Network | Retrofit | 2.12.0 | REST client |
| Network | OkHttp | 4.12.0 | HTTP client |
| Background | WorkManager | 2.10.0 | Background sync |
| Security | Security-Crypto | 1.1.0-alpha06 | Encrypted storage |
| Coroutines | kotlinx-coroutines | 1.10.2 | Async operations |
| Testing | JUnit | 4.13.2 | Test framework |
| Testing | Robolectric | 4.16.1 | JVM Android testing |
| Testing | Roborazzi | 1.59.0 | Screenshot testing |
| Lint | Spotless | 7.0.3 | Code formatting (ktfmt) |
| Lint | Detekt | 1.23.8 | Static analysis |

### 10.2 Version Catalog

All versions are managed in `gradle/libs.versions.toml`. Never hardcode versions in `build.gradle.kts` files.

> **Note**: The version table above is a snapshot as of `last_updated`. Authoritative versions live in `gradle/libs.versions.toml`. Dependabot PRs may bump versions independently.

---

## 11. Compose UI & Styling Rules

### 11.1 Material 3 Compliance

- Use only Material 3 components (`androidx.compose.material3`).
- Theme follows Material 3 color scheme with light/dark/dynamic support.
- Dynamic colors require API 31+; fallback to static scheme on older devices.

### 11.2 Accessibility & Touch Targets

- Minimum touch target: **48.dp × 48.dp** for all interactive elements.
- Content descriptions on all icons and images.
- Semantic properties on custom components.

### 11.3 Test Tags

```kotlin
Button(
    onClick = { /* ... */ },
    modifier = Modifier.testTag("submit_button")
) {
    Text("Submit")
}
```

### 11.4 State Hoisting Pattern

```kotlin
// ✅ Correct: Stateless screen, state from ViewModel
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
)

// ❌ Wrong: Screen holding state internally
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var items by remember { mutableStateOf(listOf()) }  // NO
}
```

---

## 12. Performance Considerations

### 12.1 Compose Performance

- Use `key()` in lazy lists for stable item identity.
- Avoid allocations in Composable functions (use `remember`).
- Use `derivedStateOf` for computed state from multiple flows.

### 12.2 Room Performance

- Use `Flow<List<T>>` for reactive queries.
- Add indices on frequently queried columns.
- Use `@Transaction` for multi-table operations.

### 12.3 Network Performance

- OkHttp connection pooling (default).
- Response caching via OkHttp cache.

---

## 13. Accessibility

- All images have `contentDescription`.
- Interactive elements have minimum 48.dp touch targets.
- Color contrast ratios meet WCAG 2.1 AA.
- Screen reader support via Compose semantics.
- Dynamic font scaling supported.

---

## 14. Extensibility Points

### 14.1 Adding a New API Service

```
1. Define interface in network/*ApiService.kt
2. Add Retrofit instance in GeminiClient pattern
3. Create Repository wrapping the service
4. Inject Repository into UseCase/ViewModel
```

### 14.2 Adding a New Room Entity

```
1. Define @Entity in db/*Entity.kt
2. Define @Dao in db/*Dao.kt
3. Add entity to @Database annotation in AppDatabase.kt
4. Bump database version and add migration
5. Create Repository for the entity
```

### 14.3 Adding a New Sync Worker

```
1. Create Worker class in sync/*SyncWorker.kt
2. Register in Application class
3. Schedule reactively from Repository (not UI)
4. Handle PENDING_SYNC → IN_PROGRESS → SYNCED/FAILED transitions
```

### 14.4 Adding a New Screen

```
1. Create *Screen.kt in ui/<feature>/
2. Create *ViewModel.kt with StateFlow<UiState>
3. Add NavHost route in MainActivity
4. Use manual constructor injection for ViewModel dependencies
5. Add test tags to all interactive elements
```

---

## 15. Glossary

| Term | Definition |
|---|---|
| **Offline-first** | App works fully without network; sync enriches but never overwrites local state |
| **Sync state** | Entity field tracking local mutation sync status (PENDING_SYNC, IN_PROGRESS, SYNCED, FAILED, CONFLICT) |
| **Harness** | `./harness.sh` — unified developer workflow script; all Gradle tasks run through it |
| **Quality gate** | `./harness.sh verify` — the CI pass/fail criterion (format + lint + test + build + coverage) |
| **UiState** | Sealed interface representing all possible screen states (Loading, Success, Error) |
| **ktfmt** | Kotlin formatter used by Spotless; configured with Google Style (2-space indent) |
| **Robolectric** | JVM-based Android testing framework; replaces instrumented tests |
| **Roborazzi** | Screenshot testing on JVM; compares Compose UI output against baselines |
