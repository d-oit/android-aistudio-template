# Android AI Studio Template

> A GitHub template for building **offline-first Android apps** powered by [Google AI Studio](https://aistudio.google.com) (Gemini API).

[![Use this template](https://img.shields.io/badge/Use%20this%20template-2ea44f?style=for-the-badge&logo=github)](https://github.com/d-oit/android-aistudio-template/generate)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 🚀 Stack

| Layer | Technology |
|---|---|
| Language | **Kotlin** |
| UI | **Jetpack Compose** + Material 3 |
| Local DB | **Room** (SQLite) |
| Networking | **Retrofit** + OkHttp |
| AI | **Gemini REST API** (via AI Studio key) + On-device fallback |
| Background | **WorkManager** |
| Security | Android Keystore + EncryptedSharedPreferences |
| DI | Manual constructor injection |
| Testing | JUnit 5 + Robolectric + Roborazzi |
| Quality | Spotless + Detekt + Android Lint + JaCoCo |
| CI/CD | GitHub Actions |

---

## 📁 Project Structure

```text
android-aistudio-template/
├── .github/
│   ├── workflows/
│   │   ├── ci.yml              # Main CI workflow
│   │   └── release.yml         # Release / APK artifact workflow
│   └── dependabot.yml
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/template/
│   │   │   │   ├── ai/         # Gemini API client + on-device fallback
│   │   │   │   ├── core/       # Utilities: crypto, network, extensions
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/  # Room DB, entities, DAOs
│   │   │   │   │   ├── remote/ # Retrofit API clients
│   │   │   │   │   ├── repository/ # Repository (single source of truth)
│   │   │   │   │   └── sync/   # WorkManager sync workers
│   │   │   │   ├── di/         # Dependency injection wiring
│   │   │   │   └── ui/
│   │   │   │       ├── components/ # Reusable Compose widgets
│   │   │   │       ├── screens/    # Navigation screens
│   │   │   │       ├── theme/      # Material 3 theme
│   │   │   │       └── viewmodel/  # StateFlow ViewModels
│   │   │   └── res/
│   │   └── test/               # JVM / Robolectric tests
│   └── build.gradle.kts
├── .agents/                    # AI agent context files
├── .plans/                     # Planning documents
├── .env.example                # Environment variable reference
├── .editorconfig
├── .gitignore
├── .codacy.yml
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── harness.sh                  # Unified developer workflow harness
├── metadata.json               # Template metadata
├── SPEC.md                     # Architecture specification
├── TASK.md                     # Development checklist
└── AGENTS.md                   # AI agent instructions
```

---

## ⚡ Quick Start

### 1. Use This Template

Click **[Use this template](https://github.com/d-oit/android-aistudio-template/generate)** → create your repo → clone it.

### 2. Prerequisites

- JDK 17 (Zulu OpenJDK recommended)
- Android Studio Meerkat (2024.3) or higher
- Android SDK 35

### 3. Configure Environment

Copy `.env.example` to `.env` and fill in your keys:

```properties
# Required: GitHub Personal Access Token (if your app uses GitHub APIs)
GITHUB_PAT=your_github_personal_access_token_here

# Required: Google AI Studio API key (https://aistudio.google.com/app/apikey)
GEMINI_API_KEY=your_gemini_api_key_here

# Optional: Codacy project token for coverage upload
CODACY_PROJECT_TOKEN=your_codacy_token_here
```

> ⚠️ **Never commit `.env` to version control.** It is listed in `.gitignore`.

### 4. Add GitHub Secrets

In your repository → **Settings → Secrets and variables → Actions**, add:

| Secret | Description |
|---|---|
| `GEMINI_API_KEY` | Google AI Studio API key |
| `GITHUB_PAT` | GitHub PAT (if using GitHub APIs) |
| `CODACY_PROJECT_TOKEN` | Codacy token (optional) |
| `KEYSTORE_FILE` | Base64-encoded release keystore (for release builds) |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias |
| `KEY_PASSWORD` | Key password |

### 5. Run Locally

Use the unified harness (mandatory, replaces raw `./gradlew`):

```bash
# Full verification gate (run before every push)
./harness.sh verify

# Build debug APK
./harness.sh build

# Run all tests
./harness.sh test

# Lint check
./harness.sh lint

# Format check (Spotless)
./harness.sh format-check

# Generate coverage report
./harness.sh coverage

# See all commands
./harness.sh help
```

---

## 🤖 Gemini AI Integration

This template ships with a **hybrid AI layer**:

1. **On-device heuristics** — instant, always available offline
2. **Gemini REST API** — cloud-based deep analysis via AI Studio key

The app automatically falls back to on-device if `GEMINI_API_KEY` is absent or the network is unavailable. See `app/src/main/java/com/example/template/ai/` for the implementation.

### Supported Models (configure in `ai/GeminiConfig.kt`)

```kotlin
object GeminiConfig {
    const val MODEL_FLASH = "gemini-2.0-flash"        // Fast, cost-efficient
    const val MODEL_PRO   = "gemini-2.5-pro"          // Deep reasoning
    const val BASE_URL    = "https://generativelanguage.googleapis.com/v1beta/"
}
```

---

## 🛡️ Code Quality

- **Spotless** — Kotlin formatting enforcement
- **Detekt** — static analysis
- **Android Lint** — security, performance, accessibility
- **JaCoCo** — test coverage reports
- **Codacy** — continuous quality gate (optional)

Reports output to `app/build/reports/`.

---

## 🧪 Testing Strategy

All tests run on the **JVM** (no emulator needed):

- Unit tests: `./harness.sh unit`
- Integration tests: `./harness.sh test` (Robolectric)
- Screenshot tests: `gradle :app:verifyRoborazziDebug`
- Record new screenshots: `gradle :app:recordRoborazziDebug`

---

## 🔄 CI/CD

The included GitHub Actions workflows:

| Workflow | Trigger | Actions |
|---|---|---|
| `ci.yml` | push / PR to `main` | Lint → Tests → Build Debug APK → Coverage |
| `release.yml` | push tag `v*` | Build signed Release APK → GitHub Release |

---

## 📄 License

MIT — see [LICENSE](LICENSE).
