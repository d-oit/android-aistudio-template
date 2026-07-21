# Android AI Studio Template

A generic, production-ready Android project template for [aistudio.google.com](https://aistudio.google.com). Use it as a starting point for any new Android project or as a reference for an existing codebase — no Gemini AI integration bundled by default.

## Features

- **Kotlin** (JVM 17) + **Jetpack Compose** + **Material 3**
- **Offline-first** architecture (Room + WorkManager + Repository pattern + MVVM)
- **Manual constructor injection** (no Hilt annotations)
- **Spotless + ktlint** code formatting enforced via CI
- **Robolectric + Roborazzi** for fast JVM-only testing (no emulator required)
- **GitHub Actions** CI/CD — lint, test, build, signed release
- **`./harness.sh`** developer workflow script
- **`.agents/skills/`** — full set of AI Studio agent skills ported from [d-oit/do-github-gist-ai-studio-android](https://github.com/d-oit/do-github-gist-ai-studio-android)

## Quick Start

### Use as a GitHub Template

1. Click **"Use this template"** on GitHub.
2. Clone your new repo.
3. Copy `.env.example` to `.env` and fill in `GH_TOKEN` (if needed for `gh` CLI).
4. Run `chmod +x harness.sh && ./harness.sh build`.

### Reference in an Existing Project

Copy the following into your project:
- `.agents/` — agent skills for AI Studio
- `AGENTS.md` — agent instructions
- `harness.sh` — developer workflow harness
- `.github/workflows/` — CI/CD pipelines

## Developer Workflow

```bash
# Format & lint
./harness.sh format
./harness.sh lint

# Run all tests
./harness.sh test

# Build debug APK
./harness.sh build

# Full verification gate (run before push)
./harness.sh verify
```

## GitHub Authentication

This template uses **`GH_TOKEN`** (not `GITHUB_PAT`) for all `gh` CLI operations.
Add `GH_TOKEN` as a GitHub Actions secret and export it locally in your shell.

```bash
export GH_TOKEN=ghp_...
```

See `.agents/skills/gh/SKILL.md` for full `gh` CLI usage patterns.

## Secrets

| Secret | Required | Purpose |
|---|---|---|
| `GH_TOKEN` | Optional | `gh` CLI GitHub operations |
| `CODACY_PROJECT_TOKEN` | Optional | Coverage reporting |
| `KEYSTORE_FILE` | Release only | Signed APK builds |
| `KEYSTORE_PASSWORD` | Release only | Keystore password |
| `KEY_ALIAS` | Release only | Key alias |
| `KEY_PASSWORD` | Release only | Key password |

## Architecture

```
UI (Compose) → ViewModel (StateFlow) → Repository → [Room | Retrofit | RemoteDataSource]
```

## Agent Skills

All agent skills from `.agents/skills/` are available for AI Studio:

| Skill | Purpose |
|---|---|
| `gh` | GitHub CLI patterns for agents |
| `android-cli` | Android CLI tooling |
| `adaptive` | Adaptive layouts |
| `agp-9-upgrade` | AGP 9 migration |
| `android-intent-security` | Intent security best practices |
| `appfunctions` | App Functions integration |
| `camerax` | CameraX usage |
| `edge-to-edge` | Edge-to-edge display |
| `engage-sdk-integration` | Engage SDK |
| `migrate-xml-views-to-jetpack-compose` | XML → Compose migration |
| `navigation-3` | Navigation 3 |
| `perfetto-sql` | Perfetto SQL tracing |
| `perfetto-trace-analysis` | Trace analysis |
| `play-billing-library-version-upgrade` | Billing library upgrade |
| `play-policy-insights` | Play policy |
| `r8-analyzer` | R8 shrinking/analysis |
| `styles` | Styling guidelines |
| `testing-setup` | Test infrastructure |
| `verified-email` | Verified email flows |
| `wear-compose-m3` | Wear OS Compose M3 |

## License

MIT — see [LICENSE](LICENSE).
