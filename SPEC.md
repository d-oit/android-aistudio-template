# Architecture Specification

## 1. Overview

This template delivers an **offline-first Android application** with optional **Gemini AI cloud enrichment** via Google AI Studio. The architecture follows the Android recommended app architecture (MVVM + Repository) with explicit offline/online state management.

---

## 2. Core Principles

- **Offline-first**: Local Room DB is the single source of truth. Network operations only enrich, never replace, local state.
- **Graceful degradation**: Gemini AI features degrade transparently to on-device heuristics.
- **Security by design**: API keys injected at build time via `BuildConfig`; PATs encrypted with Android Keystore.
- **Testability**: All layers mockable; no static singletons in production code paths.

---

## 3. Layer Responsibilities

| Layer | Classes | Responsibility |
|---|---|---|
| UI | `Screen*`, `*ViewModel` | Render state, emit actions |
| Domain | `*UseCase` | Business logic, AI prompt assembly |
| Data | `*Repository` | Orchestrate local + remote sources |
| Local | `*Dao`, `*Entity` | Room persistence |
| Remote | `*ApiService` | Retrofit REST clients |
| AI | `AiRepository`, `GeminiClient` | Gemini cloud + on-device fallback |
| Sync | `*SyncWorker` | WorkManager background sync |

---

## 4. AI Integration State Machine

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

## 5. Security

- `GEMINI_API_KEY` and `GITHUB_PAT` injected via `BuildConfig` (from `.env` or CI secrets)
- User-facing PATs stored encrypted: `MasterKey` + `EncryptedSharedPreferences`
- All network traffic over HTTPS; OkHttp certificate pinning recommended for production

---

## 6. Testing Strategy

| Scope | Framework | Location |
|---|---|---|
| Unit | JUnit 5 + Mockito | `app/src/test/` |
| Integration | Robolectric | `app/src/test/` |
| Screenshot | Roborazzi | `app/src/test/` |
| Coverage | JaCoCo | CI + local harness |

---

## 7. Agent Skills

AI coding agents working on this project can optionally install the [android/skills](https://github.com/android/skills) skill pack via:

```bash
gh skill install android/skills
```

See [gh skill install](https://cli.github.com/manual/gh_skill_install) for the full CLI reference. Any additional skills follow the same pattern:

```bash
gh skill install <owner>/<repo> [<skill-name>]
```
