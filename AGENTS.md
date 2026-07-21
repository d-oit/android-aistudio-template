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
- **`/DESIGN.md`**: The machine-readable design specification (YAML frontmatter + structured sections). Contains architecture, data model, ADRs, testing strategy, CI/CD, and coding conventions. **Read this first.**
- **`/SPEC.md`**: Contains the master design specification, entity relationships, offline-first synchronization state machine flags, and styling guidelines.
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

## 8. Agent Skills

### Android Skills (Optional)

The [android/skills](https://github.com/android/skills) repository provides curated agent skills covering Jetpack Compose patterns, Room, WorkManager, architecture guidelines, and more. These are **optional** but strongly recommended when working on this template.

### Installing Other Skills

Any additional skills (e.g., for CI, testing frameworks, or language tooling) can be installed the same way:

```bash
gh skill install <owner>/<repo>
gh skill install <owner>/<repo> <skill-name>
```

Installed skills are placed under `.agents/skills/` and are automatically loaded by AI Studio agents at session start.

---

## 9. Adding a New Feature

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

---

## 10. Template Onboarding: First Steps for New Projects

When a new codebase is created from this template (via GitHub "Use this template" or fork) and imported into AI Studio, agents **must** complete this checklist before implementing any features.

### 10.1 Read the Specification Files

```
Priority order:
1. AGENTS.md   — mandatory rules, constraints, verification loop
2. DESIGN.md   — architecture, data model, ADRs, coding conventions
3. TASK.md     — development progress, pending requirements
4. SPEC.md     — entity relationships, sync state machine
5. .agents/context.md — quick tech stack reference
```

### 10.2 Customize the Template Identity

The template ships with placeholder values that **must** be changed:

| What | Where | From | To |
|---|---|---|---|
| **Application ID** | `app/build.gradle.kts` line `applicationId` | `com.example.template` | Your reverse-domain ID |
| **Namespace** | `app/build.gradle.kts` line `namespace` | `com.example.template` | Your namespace |
| **Package directory** | `app/src/main/java/com/example/template/` | `com/example/template/` | Your package path |
| **Test package** | `app/src/test/java/com/example/template/` | `com/example/template/` | Your package path |
| **App name** | `app/src/main/res/values/strings.xml` | `AndroidAiStudioTemplate` | Your app name |
| **Theme name** | `app/src/main/res/values/themes.xml` | `Theme.AndroidAiStudioTemplate` | `Theme.YourAppName` |
| **AndroidManifest** | `app/src/main/AndroidManifest.xml` | `android:label="@string/app_name"` | Update after strings.xml |
| **Project name** | `settings.gradle.kts` line `rootProject.name` | `AndroidAiStudioTemplate` | Your project name |
| **Gemini key** | `.env` or CI secrets | `GEMINI_API_KEY=your_...` | Your actual API key |

**Steps:**

```bash
# 1. Rename package directories
mkdir -p app/src/main/java/com/your/app
mkdir -p app/src/test/java/com/your/app
mv app/src/main/java/com/example/template/* app/src/main/java/com/your/app/
mv app/src/test/java/com/example/template/* app/src/test/java/com/your/app/
rm -rf app/src/main/java/com/example
rm -rf app/src/test/java/com/example

# 2. Update package declarations in all .kt files
find app/src -name '*.kt' -exec sed -i 's/com.example.template/com.your.app/g' {} +

# 3. Update build.gradle.kts
#    Change namespace and applicationId from com.example.template to com.your.app

# 4. Update settings.gradle.kts
#    Change rootProject.name to your project name

# 5. Update strings.xml and themes.xml with your app name

# 6. Update AndroidManifest.xml theme reference

# 7. Verify with: ./harness.sh build
```

### 10.3 Configure Secrets

```bash
# Copy .env.example to .env
cp .env.example .env

# Add your Gemini API key (optional — on-device fallback works without it)
# Edit .env and set GEMINI_API_KEY=your_actual_key

# Add GH_TOKEN if using gh CLI locally
# Edit .env and set GH_TOKEN=ghp_your_token
```

### 10.4 Run the Verification Loop

```bash
# Ensure harness.sh is executable
chmod +x harness.sh

# Full verification pipeline
./harness.sh verify
```

### 10.5 Set Up Git

```bash
# If starting fresh from the template
git remote set-url origin https://github.com/YOUR_ORG/YOUR_REPO.git
git push -u origin main

# Install pre-push hooks
./harness.sh setup-hooks
```

### 10.6 Verify CI

After pushing, verify that all GitHub Actions CI checks pass:
- Use `gh pr checks` (with `GH_TOKEN`) to monitor CI status
- All checks must pass before declaring any task done

### 10.7 What NOT to Change

These files are template infrastructure — keep them as-is unless you have a specific reason:

- `.github/workflows/` — CI/CD pipelines
- `.github/dependabot.yml` — dependency update config
- `harness.sh` — developer workflow script
- `config/detekt.yml` — static analysis config
- `.codacy.yml` — code quality config
- `build.gradle.kts` dependency constraints block — security overrides
- `.agents/skills/` — agent skill definitions
