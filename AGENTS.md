# AI Agent Instructions — Android AI Studio Template

This file provides guidance for AI coding agents (Gemini in AI Studio, Copilot, Claude, Codex, etc.) working within this Android template repository.

---

## Project Overview

- **Language**: Kotlin (JVM target 17)
- **UI**: Jetpack Compose + Material 3
- **Architecture**: Offline-first, Repository pattern, MVVM
- **Quality Gate**: All changes must pass `./harness.sh verify` before pushing

---

## 1. MANDATORY: Read Specifications & Track Tasks First

Before executing any structural modifications, feature updates, or database schema additions, you **MUST** read and cross-reference the master files:
- **`/SPEC.md`**: Contains the master design specification, entity relationships, offline-first synchronization state machine flags, and styling guidelines. Read this first to align design and data models.
- **`/TASK.md`**: Tracks development progress, completed items, and pending requirements. Keep this file updated as features are implemented or adjusted.

---

## 2. Key Rules for AI Agents

1. **Never commit `.env`** or any file containing API keys, tokens, or passwords.
2. **Always use `./harness.sh`** for build/test/lint — never invoke `./gradlew` directly.
3. **API keys** are injected via `BuildConfig` — never hardcode them.
4. **Offline-first**: Every feature must work without network; add AI/cloud features as optional enhancements.
5. **Test coverage**: Add JUnit/Robolectric tests for all repository and ViewModel logic.
6. **Compose-first UI**: Do not add XML layouts; use Jetpack Compose exclusively.
7. **Room for persistence**: All local state goes through Room DAOs.
8. **WorkManager for background**: All sync operations use WorkManager workers.
9. **GH_TOKEN only**: For all GitHub CLI (`gh`) operations use `GH_TOKEN`.

---

## 3. Non-Negotiable Constraints

1. **No Hilt Annotations**:
   - **Never write `@Inject`, `@HiltViewModel`, `@AndroidEntryPoint`, or `@HiltAndroidApp`** in new or modified files.
   - The codebase utilizes **manual constructor injection**.
   - New services and components must be instantiated in the Application class and passed explicitly to their respective factories.

2. **Strict Sync State Control**:
   - Every local mutation must route exclusively through the repository using the sync state flags.
   - Do not write ad-hoc logic that bypasses these state transition flags.

3. **No UI-Triggered Sync Workers**:
   - Composable screens and ViewModels **must not** schedule sync workers or call remote APIs directly.
   - All synchronization is reactively driven.

4. **No Instrumented Tests (`androidTest/`)**:
   - All tests must run locally on the JVM via Robolectric and Roborazzi.
   - Do not write or attempt to run any instrumented tests under `/app/src/androidTest/`.

5. **MANDATORY: Always Implement and Run the Test Pyramid (E2E Focus)**:
   - Always design, implement, and run the complete automated Test Pyramid when introducing new features.
   - Local JVM Robolectric E2E tests are the highest-fidelity validation gate.

---

## 4. Developer Verification Loop (Definition of Done)

Before declaring any change completed, execute:

1. **Lint/Check**: `./harness.sh check`
2. **Spotless Formatting (MANDATORY)**: `./harness.sh format`
3. **Pre-Push Gate (MANDATORY)**: `./harness.sh setup-hooks`
4. **Build**: `./harness.sh build`
5. **E2E / Test Pyramid Run**: `./harness.sh test` or `./harness.sh e2e`
6. **Screenshot Verification**: If Compose UI layouts were intentionally changed, verify or re-record Roborazzi screenshot baselines.
7. **PR Git Push & CI Verification (MANDATORY)**:
   - Always `git commit` and `git push` changes to the Pull Request branch.
   - **All CI checks must pass completely** before a task is declared done.
   - Use `gh pr checks` (authenticated via `GH_TOKEN`) to verify PR status.

---

## 5. Compose UI & Styling Rules

- **Material 3 Only**: Use only Material 3 components.
- **Touch Targets**: Every interactive element must maintain a minimum touch target size of `48.dp x 48.dp`.
- **Test Tags**: Add `Modifier.testTag("snake_case_name")` to all clickable, interactive, or text-input components.
- **State Hoisting**: Keep screens state-free; hoist state to ViewModel as a read-only `StateFlow`.

---

## 6. Code Quality & Best Practices

1. **Maximum 600 LOC for Source Files**.
2. **No Hardcoded Settings or Magic Numbers**.
3. **Always Use Latest Best Practices**: Modern Android, Kotlin Coroutines, Flow, Jetpack Compose, and Room.
4. **Strict Spotless & ktlint Compliance (CRITICAL)**: Always run `gradle spotlessApply` before pushing.

---

## 7. Secrets Management

| Secret | Where |
|---|---|
| GH_TOKEN | Shell env / GitHub Actions secret — used by `gh` CLI |
| CODACY_PROJECT_TOKEN | Shell env / GitHub Actions secret |
| Keystore | `keystore/` dir (gitignored) → GitHub Actions secret (base64) |

---

## 8. Adding a New Feature

```
1. Define the use-case / domain logic in a dedicated UseCase class
2. Inject Repository (constructor injection)
3. Handle both online and offline states in the ViewModel
4. Expose result via StateFlow<UiState>
5. Add unit tests mocking the Repository
```

---

## Architecture Layers

```
UI (Compose) → ViewModel (StateFlow) → Repository → [Room | Retrofit | RemoteDataSource]
```
