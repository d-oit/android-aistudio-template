# AI Agent Instructions

This file provides guidance for AI coding agents (Gemini in AI Studio, Copilot, Claude, Codex, etc.) working within this Android template repository.

---

## Project Overview

- **Language**: Kotlin (JVM target 17)
- **UI**: Jetpack Compose + Material 3
- **AI Integration**: Gemini REST API (AI Studio key) with on-device fallback
- **Architecture**: Offline-first, Repository pattern, MVVM
- **Quality Gate**: All changes must pass `./harness.sh verify` before pushing

---

## Key Rules for AI Agents

1. **Never commit `.env`** or any file containing API keys, tokens, or passwords.
2. **Always use `./harness.sh`** for build/test/lint — never invoke `./gradlew` directly.
3. **Gemini API key** is injected via `BuildConfig.GEMINI_API_KEY` — never hardcode it.
4. **Offline-first**: Every feature must work without network; add Gemini AI as optional enhancement.
5. **Test coverage**: Add JUnit/Robolectric tests for all repository and ViewModel logic.
6. **Compose-first UI**: Do not add XML layouts; use Jetpack Compose exclusively.
7. **Room for persistence**: All local state goes through Room DAOs.
8. **WorkManager for background**: All sync operations use WorkManager workers.

---

## Adding a New Gemini-Powered Feature

```
1. Define prompt in a dedicated UseCase class under ai/
2. Inject AiRepository (constructor injection)
3. Handle both AiResult.Cloud and AiResult.OnDevice in the ViewModel
4. Expose result via StateFlow<UiState>
5. Add unit tests mocking AiRepository
```

---

## Architecture Layers

```
UI (Compose) → ViewModel (StateFlow) → Repository → [Room | Retrofit | AiRepository]
```

---

## Secrets Management

| Secret | Where |
|---|---|
| GEMINI_API_KEY | `.env` locally → GitHub Actions secret → `BuildConfig` |
| GITHUB_PAT | `.env` locally → GitHub Actions secret → `BuildConfig` |
| CODACY_PROJECT_TOKEN | Shell env / GitHub Actions secret |
| Keystore | `keystore/` dir (gitignored) → GitHub Actions secret (base64) |
