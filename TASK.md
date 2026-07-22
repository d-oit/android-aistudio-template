# Development Checklist

> **See also**: `DESIGN.md` for comprehensive architecture and coding conventions.

## Phase 0 — Template Bootstrap ✅
- [x] Repository structure
- [x] Gradle version catalog
- [x] `harness.sh` workflow script
- [x] GitHub Actions CI/CD workflows
- [x] `.env.example` with GEMINI_API_KEY
- [x] Dependabot config
- [x] Detekt + Spotless config
- [x] `AGENTS.md` for AI coding assistants
- [x] `DESIGN.md` machine-readable design specification (Stitch format)
- [x] Dependabot security remediation (49 alerts — 2 critical, 10 high)
- [x] Template onboarding for AI Studio agents (AGENTS.md §10, README.md, context.md)
- [x] Quickstart guide for customizing template into a real Android app (`QUICKSTART.md`)

## Phase 1 — Core App Scaffold
- [ ] `MainActivity` with Compose `NavHost`
- [ ] Material 3 theme (light/dark/dynamic)
- [ ] Room DB setup with sample entity + DAO
- [ ] `AiRepository` integration test
- [ ] ViewModel with StateFlow pattern example
- [ ] WorkManager sync worker skeleton

## Phase 2 — Gemini Feature
- [ ] Prompt builder utility
- [ ] `GeminiUseCase` example (e.g. text summarization)
- [ ] On-device fallback implementation (MediaPipe / ML Kit)
- [ ] UI screen demonstrating AI result display
- [ ] Unit tests for `AiRepository`

## Phase 3 — Quality & CI Polish
- [ ] JaCoCo coverage threshold enforcement (≥ 70%)
- [ ] Roborazzi screenshot baseline
- [ ] Codacy integration docs
- [ ] ProGuard / R8 rules for Retrofit + Gemini
- [ ] README badges (CI, coverage, Codacy grade)

## Phase 4 — Distribution
- [ ] Signed release keystore setup guide
- [ ] `release.yml` workflow validation
- [ ] GitHub Releases APK artifact test
- [ ] Template repository flag enabled on GitHub
