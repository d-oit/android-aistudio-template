# Quickstart Guide: Customizing the Template for a Real Android App

This guide explains step-by-step how to transform this template repository into a real, production-ready Android application while maintaining all built-in quality gates, CI/CD pipelines, and the `./harness.sh` developer workflow.

---

## Overview of the Customization Process

```
Template Repo
  ├── 1. Rebrand Package & App Identity  (strings.xml, build.gradle.kts, package dirs)
  ├── 2. Configure Environment Secrets   (.env, BuildConfig)
  ├── 3. Build Features & Architecture   (MVVM, Room DB, Jetpack Compose UI)
  ├── 4. Execute Verification Harness    (./harness.sh verify)
  └── 5. Enable Git Pre-Push Hook        (./harness.sh setup-hooks)
```

---

## 1. Rebranding Project Identity & Package Structure

When creating a new application from this template, update the default placeholder values (`com.example.template` and `AndroidAiStudioTemplate`) to reflect your real app.

### 1.1 Identity Mapping Table

| Resource / File | Default Placeholder | Your Real App Value Example |
|---|---|---|
| **`applicationId`** (`app/build.gradle.kts`) | `com.example.template` | `com.mycompany.myapp` |
| **`namespace`** (`app/build.gradle.kts`) | `com.example.template` | `com.mycompany.myapp` |
| **Package Directory** | `app/src/main/java/com/example/template` | `app/src/main/java/com/mycompany/myapp` |
| **Test Package Directory** | `app/src/test/java/com/example/template` | `app/src/test/java/com/mycompany/myapp` |
| **App Name** (`res/values/strings.xml`) | `AndroidAiStudioTemplate` | `My Real App` |
| **Theme Name** (`res/values/themes.xml`) | `Theme.AndroidAiStudioTemplate` | `Theme.MyRealApp` |
| **Project Name** (`settings.gradle.kts`) | `AndroidAiStudioTemplate` | `MyRealApp` |
| **AI Studio Metadata** (`metadata.json`) | `AI Studio Template` | `My Real App` |

---

### 1.2 Automated Package Renaming Script

You can run the following bash snippet from the root of the repository to automatically restructure Kotlin source directories and update package declarations:

```bash
NEW_PKG="com.mycompany.myapp"
OLD_PKG="com.example.template"

# Convert package dot notation to file paths
NEW_PATH=$(echo "$NEW_PKG" | tr '.' '/')
OLD_PATH=$(echo "$OLD_PKG" | tr '.' '/')

# 1. Create target directories
mkdir -p "app/src/main/java/$NEW_PATH"
mkdir -p "app/src/test/java/$NEW_PATH"

# 2. Move source files
if [ -d "app/src/main/java/$OLD_PATH" ]; then
  mv app/src/main/java/$OLD_PATH/* "app/src/main/java/$NEW_PATH/"
  rm -rf app/src/main/java/com/example
fi

if [ -d "app/src/test/java/$OLD_PATH" ]; then
  mv app/src/test/java/$OLD_PATH/* "app/src/test/java/$NEW_PATH/"
  rm -rf app/src/test/java/com/example
fi

# 3. Replace package declarations across Kotlin files
find app/src -name '*.kt' -exec sed -i "s/$OLD_PKG/$NEW_PKG/g" {} +
```

---

### 1.3 Configuration File Edits

#### `app/build.gradle.kts`
Update `namespace` and `applicationId`:
```kotlin
android {
    namespace = "com.mycompany.myapp"
    defaultConfig {
        applicationId = "com.mycompany.myapp"
        // ...
    }
}
```

#### `app/src/main/res/values/strings.xml`
Update your app launcher label:
```xml
<resources>
    <string name="app_name">My Real App</string>
</resources>
```

#### `settings.gradle.kts`
Update the root Gradle project name:
```kotlin
rootProject.name = "MyRealApp"
```

#### `metadata.json`
Keep AI Studio platform metadata strictly synced with `strings.xml`:
```json
{
  "name": "My Real App",
  "description": "Description of your real Android application",
  "majorCapabilities": [
    "MAJOR_CAPABILITY_SERVER_SIDE_GEMINI_API"
  ]
}
```

---

## 2. Environment Variables & Secrets Management

Secrets are managed securely without committing credentials to version control.

### 2.1 Local Secret Setup
1. Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```
2. Populate `.env` with required keys:
   ```ini
   GEMINI_API_KEY=your_gemini_api_key_here
   GH_TOKEN=your_github_token_here
   ```

### 2.2 Injected `BuildConfig` Fields
The `app/build.gradle.kts` automatically reads `.env` variables and exposes them via `BuildConfig`:
- Access `BuildConfig.GEMINI_API_KEY` in code.
- Access `BuildConfig.GH_TOKEN` in scripts or test utilities.
- Never hardcode secrets in Kotlin code or XML files.

---

## 3. Developer Workflow Harness (`./harness.sh`)

All project tasks are executed through `./harness.sh`. Do **not** run raw `./gradlew` commands for standard workflows.

### 3.1 Harness Command Reference

| Command | Action | When to Use |
|---|---|---|
| `./harness.sh verify` | **Full CI Pipeline Gate**: Format Check → Lint → Test → Build → Coverage | Run before pushing PRs or releasing |
| `./harness.sh check` | **Static Analysis Gate**: Format Check → Lint → Test Pyramid | Quick validation during coding |
| `./harness.sh format` | Applies Spotless formatting and ktlint auto-fixes | When format checks fail |
| `./harness.sh format-check` | Verifies code formatting compliance | CI static checks |
| `./harness.sh lint` | Runs Android Lint (`:app:lintDebug`) | Inspecting UI and Android system warnings |
| `./harness.sh detekt` | Runs Detekt static code analysis | Inspecting complexity & code smells |
| `./harness.sh test` | Runs all unit and Robolectric JVM tests | Validating business logic and state |
| `./harness.sh unit` | Runs standard JVM unit tests | Rapid unit test feedback |
| `./harness.sh e2e` | Runs high-fidelity local JVM Robolectric E2E tests | Validating full user flows without emulator |
| `./harness.sh build` | Compiles and builds the Debug APK | Verifying app compilation |
| `./harness.sh build-release` | Builds signed Release APK (requires keystore env) | Preparing production release build |
| `./harness.sh coverage` | Generates JaCoCo test coverage HTML & XML reports | Checking test code coverage |
| `./harness.sh setup-hooks` | Installs Git pre-push hook running `./harness.sh check` | Initial setup after cloning |
| `./harness.sh wrapper-check` | Validates integrity of `gradle-wrapper.jar` | Security & build system verification |
| `./harness.sh clean` | Cleans all build build artifacts and caches | Resolving stale build issues |

---

## 4. Architecture & Feature Development Roadmap

Follow the established MVVM + Repository pattern when adding new features:

```
UI (Jetpack Compose)
   └── ViewModel (StateFlow)
        └── Repository (Offline-first data sync)
             ├── Local Data Source (Room DB / DAOs)
             └── Remote Data Source (Retrofit / Ktor / Gemini API)
```

### 4.1 Dependency Injection Rules
- **No Hilt Annotations**: Do not write `@Inject`, `@HiltViewModel`, or `@AndroidEntryPoint`.
- Use **Manual Constructor Injection**. Instantiating repositories and ViewModels cleanly via factory functions or custom ViewModel factories ensures fast local JVM Robolectric testing.

### 4.2 Room Database Persistence
1. Add `@Entity` data classes in your model layer.
2. Define `@Dao` interfaces with Coroutines `Flow` return types.
3. Include DAOs in `AppDatabase : RoomDatabase()`.

### 4.3 High-Fidelity JVM Testing (Robolectric)
- All unit, integration, and E2E tests run locally on JVM without an Android Emulator.
- Execute E2E tests via:
  ```bash
  ./harness.sh e2e
  ```

---

## 5. First-Time Verification Checklist

After completing the customization steps, execute the full verification loop:

```bash
# 1. Format code to comply with Spotless/ktlint
./harness.sh format

# 2. Run full static analysis & test pyramid
./harness.sh check

# 3. Build Debug APK
./harness.sh build

# 4. Run full verification pipeline gate
./harness.sh verify

# 5. Install Git pre-push hook
./harness.sh setup-hooks
```

Once all steps succeed with green output, your customized application is ready for feature development and production deployment!
