# Android AI Studio Template

A generic, production-ready Android project template for [aistudio.google.com](https://aistudio.google.com/). Use it as a starting point for any new Android project or as a reference for an existing codebase — no Gemini AI integration bundled by default.

## Features

- **Kotlin** (JVM 17) + **Jetpack Compose** + **Material 3**
- **Offline-first** architecture (Room + WorkManager + Repository pattern + MVVM)
- **Manual constructor injection** (no Hilt annotations)
- **Spotless + ktlint** code formatting enforced via CI
- **Robolectric + Roborazzi** for fast JVM-only testing (no emulator required)
- **GitHub Actions** CI/CD — lint, test, build, signed release
- **`./harness.sh`** developer workflow script
- **GitHub agent skills** for project-local AI workflows

## Quick Start

### Use as a GitHub Template

1. Click **Use this template** on GitHub.
2. Clone your new repo.
3. Copy `.env.example` to `.env` and fill in `GH_TOKEN` if needed for `gh` CLI usage. Recommndation: use aistudio.google.com / github secrets instead of .env
4. Run `chmod +x harness.sh && ./harness.sh build`.
5. Optionally install or update project-local agent skills as described below.

### Reference in an Existing Project

Copy the following into your project:

- `.agents/` — local agent skills and agent configuration
- `AGENTS.md` — agent instructions
- `harness.sh` — developer workflow harness
- `.github/workflows/` — CI/CD pipelines

## Agent Skills

This template is designed for **project-local** agent skills managed with the GitHub CLI.

### Install local project skills

From the project root, install all skills published by this repository into the current project directory:

```bash
gh skill install . --dir .
```

This makes `gh` scan the current repository for supported skill layouts and install discovered skills into the project-local agent directory.

### Update all project skills

To update every installed agent skill in the current project, run:

```bash
gh skill update --dir .
```

Use this after pulling changes to `.agents/`, `AGENTS.md`, or any installed skill sources.

### About `android/skills`

The `android/skills` repository is useful as a reference for Android-focused agent guidance, but it does **not** follow the repository layout that `gh skill install` expects for skill discovery in all cases. If `gh skill install android/skills --allow-hidden-dirs` reports `no skills found`, use this template's local skills instead or manually copy the needed Android skill content into a compatible project-local skill directory.

### Installing other skills

For repositories that follow the standard GitHub Skills publisher layout, use:

```bash
gh skill install OWNER/REPO --dir .
```

To install a specific published skill from a compatible repository:

```bash
gh skill install OWNER/REPO SKILL_NAME --dir .
```

Installed skills are stored in the project-local agent skill directory and can then be refreshed with `gh skill update --dir .`.

## Using This Template in AI Studio

This repository is designed to be imported into [Google AI Studio](https://aistudio.google.com/) as a starting point for Android app development with AI coding agents.

### First Steps After Creating a New Project

1. **Click "Use this template"** on GitHub to create your new repository.
2. **Import your new repo** into AI Studio.
3. **The agent will read `AGENTS.md`** — it contains the full onboarding checklist in §10.
4. **Key customization** (the agent will guide you):
   - Rename package: `com.example.template` → your package name
   - Update app name in `strings.xml`
   - Update project name in `settings.gradle.kts`
   - Configure `.env` with your API keys
   - Run `./harness.sh verify` to confirm everything works

### What the Template Provides

| Component | Description |
|---|---|
| `AGENTS.md` | **Agent instructions** — mandatory rules, constraints, onboarding checklist |
| `DESIGN.md` | Machine-readable architecture spec (YAML frontmatter + structured sections) |
| `TASK.md` | Development progress tracker — update as features are built |
| `SPEC.md` | Entity relationships, sync state machine, styling guidelines |
| `.agents/context.md` | Quick tech stack reference for agents |
| `.agents/skills/` | Installed agent skills (gh CLI, skill management) |
| `harness.sh` | Unified developer workflow script |
| `.github/workflows/` | CI/CD pipelines (lint, test, build, release) |

### Agent Workflow in AI Studio

```
Agent session starts
  → Reads AGENTS.md, DESIGN.md, TASK.md, .agents/context.md
  → Understands architecture, constraints, and current progress
  → Implements features following the MVVM + Repository pattern
  → Verifies with ./harness.sh verify before marking tasks done
```

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

This template uses **`GH_TOKEN`** for `gh` CLI operations. Add `GH_TOKEN` as a GitHub Actions secret and export it locally in your shell.

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

`UI (Compose) → ViewModel (StateFlow) → Repository → [Room | Retrofit | RemoteDataSource]`

## License

MIT — see [LICENSE](https://github.com/d-oit/android-aistudio-template/blob/main/LICENSE).
