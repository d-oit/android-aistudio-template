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
- **[android/skills](https://github.com/android/skills)** (optional) — curated Android agent skills for AI Studio

## Quick Start

### Use as a GitHub Template

1. Click **"Use this template"** on GitHub.
2. Clone your new repo.
3. Copy `.env.example` to `.env` and fill in `GH_TOKEN` (if needed for `gh` CLI).
4. Run `chmod +x harness.sh && ./harness.sh build`.
5. *(Optional)* Install Android agent skills — see [Agent Skills](#agent-skills) below.

### Reference in an Existing Project

Copy the following into your project:
- `.agents/` — agent skills for AI Studio
- `AGENTS.md` — agent instructions
- `harness.sh` — developer workflow harness
- `.github/workflows/` — CI/CD pipelines

## Agent Skills

This template supports the [GitHub Skills](https://cli.github.com/manual/gh_skill_install) system for AI coding agents.

### Android Skills (Optional)

The [android/skills](https://github.com/android/skills) repository provides curated agent skills for Android development (Compose, Room, architecture patterns, etc.).

Install via the `gh` CLI:

```bash
# Install all Android skills
gh skill install android/skills

# Install a specific skill
gh skill install android/skills <skill-name>
```

See the [gh skill install documentation](https://cli.github.com/manual/gh_skill_install) for full usage.

### Installing Other Skills

Any skill from a public GitHub repository can be installed the same way:

```bash
gh skill install <owner>/<repo>
gh skill install <owner>/<repo> <skill-name>
```

Installed skills are stored under `.agents/skills/` and are automatically picked up by AI Studio agents.

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

This template uses **`GH_TOKEN`** for all `gh` CLI operations.
Add `GH_TOKEN` as a GitHub Actions secret and export it locally in your shell.

```bash
export GH_TOKEN=ghp_...
```

See `.agents/skills/gh/SKILL.md` for full `gh` CLI usage patterns.

## Secrets

| Secret | Required | Purpose |
|---|---|---|
| `GH_TOKEN` | Optional | `gh` CLI GitHub operations |
| `CODACY_PROJECT_TOKEN` | Optional | Code Quality / Quality Gate (free for open source / public codebase) |
| `KEYSTORE_FILE` | Release only | Signed APK builds |
| `KEYSTORE_PASSWORD` | Release only | Keystore password |
| `KEY_ALIAS` | Release only | Key alias |
| `KEY_PASSWORD` | Release only | Key password |

## Architecture

```
UI (Compose) → ViewModel (StateFlow) → Repository → [Room | Retrofit | RemoteDataSource]
```

## License

MIT — see [LICENSE](LICENSE).
