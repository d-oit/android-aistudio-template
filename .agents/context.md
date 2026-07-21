# Agent Context

## Repository Purpose
Android GitHub Template for Google AI Studio integration. Enables rapid bootstrapping of offline-first Android apps with optional Gemini AI capabilities. **This is a template repository** — new projects are created by forking or using "Use this template" on GitHub, then customizing package names and app identity.

## Template Onboarding (for AI Studio Agents)
When a new codebase is created from this template and imported into AI Studio:
1. Read `AGENTS.md` first — it contains all mandatory rules and constraints
2. Read `DESIGN.md` — the machine-readable architecture specification
3. Read `TASK.md` — tracks development progress and pending work
4. See `AGENTS.md` §10 "Template Onboarding" for the full customization checklist

## AI Studio Integration
- API: `https://generativelanguage.googleapis.com/v1beta/`
- Key: `BuildConfig.GEMINI_API_KEY` (injected from `.env` or CI secret)
- Docs: https://ai.google.dev/gemini-api/docs

## Tech Stack Quick Reference
- Kotlin 2.2.10, AGP 9.3.0, Gradle 9.6.1, compileSdk 36, minSdk 26, JDK 21
- Compose BOM 2025.05.00, Material 3 1.3.2
- Room 2.7.0, Retrofit 2.12.0, OkHttp 4.12.0
- WorkManager 2.10.0, Security Crypto 1.1.0-alpha06
- Robolectric 4.16.1, Roborazzi 1.59.0
- Spotless 7.0.3, Detekt 1.23.8

## Do Not
- Add XML layouts
- Hardcode API keys
- Invoke ./gradlew directly (use harness.sh)
- Commit .env
- Use Hilt annotations (manual constructor injection only)
