# Agent Context

## Repository Purpose
Android GitHub Template for Google AI Studio integration. Enables rapid bootstrapping of offline-first Android apps with Gemini AI capabilities.

## AI Studio Integration
- API: `https://generativelanguage.googleapis.com/v1beta/`
- Key: `BuildConfig.GEMINI_API_KEY` (injected from `.env` or CI secret)
- Docs: https://ai.google.dev/gemini-api/docs

## Tech Stack Quick Reference
- Kotlin 2.0.21, AGP 8.7.3, compileSdk 35, minSdk 26
- Compose BOM 2025.05.00, Material 3
- Room 2.7.1, Retrofit 2.11.0, OkHttp 4.12.0
- WorkManager 2.10.1, Security Crypto 1.1.0-alpha06
- Robolectric 4.14.1, Roborazzi 1.44.0

## Do Not
- Add XML layouts
- Hardcode API keys
- Invoke ./gradlew directly (use harness.sh)
- Commit .env
