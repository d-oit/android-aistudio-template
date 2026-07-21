#!/usr/bin/env bash
# =============================================================================
# Android AI Studio Template — Unified Developer Workflow Harness
# =============================================================================
# All local development commands run through this script.
# Direct ./gradlew invocations should NOT be used for standard tasks.
# =============================================================================
set -euo pipefail

# Load .env if present
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
fi

COMMAND="${1:-help}"

case "$COMMAND" in
  help)
    echo "Android AI Studio Template — Harness Commands:"
    echo "  verify       Full gate: format-check → lint → test → build"
    echo "  check        Static gate: format-check → lint → test"
    echo "  build        Build debug APK"
    echo "  test         Run full test suite (unit + Robolectric)"
    echo "  unit         Run JVM unit tests only"
    echo "  lint         Android Lint"
    echo "  format-check Spotless format check"
    echo "  format-apply Auto-fix formatting"
    echo "  coverage     Generate JaCoCo coverage report"
    echo "  codacy       Upload coverage to Codacy"
    echo "  clean        Clean build artifacts"
    ;;
  verify)
    ./harness.sh format-check
    ./harness.sh lint
    ./harness.sh test
    ./harness.sh build
    ./harness.sh coverage
    ;;
  check)
    ./harness.sh format-check
    ./harness.sh lint
    ./harness.sh test
    ;;
  build)
    ./gradlew assembleDebug
    ;;
  test)
    ./gradlew testDebugUnitTest
    ;;
  unit)
    ./gradlew :app:testDebugUnitTest
    ;;
  lint)
    ./gradlew lint
    ;;
  format-check)
    ./gradlew spotlessCheck
    ;;
  format-apply)
    ./gradlew spotlessApply
    ;;
  coverage)
    ./gradlew jacocoTestReportDebug
    echo "HTML report: app/build/reports/jacoco/jacocoTestReportDebug/html/index.html"
    if [ -n "${CODACY_PROJECT_TOKEN:-}" ]; then
      ./harness.sh codacy
    fi
    ;;
  codacy)
    if [ -z "${CODACY_PROJECT_TOKEN:-}" ]; then
      echo "CODACY_PROJECT_TOKEN not set, skipping upload."
      exit 0
    fi
    bash <(curl -Ls https://coverage.codacy.com/get.sh) report \
      -r app/build/reports/jacoco/jacocoTestReportDebug/jacocoTestReportDebug.xml
    ;;
  clean)
    ./gradlew clean
    ;;
  *)
    echo "Unknown command: $COMMAND"
    ./harness.sh help
    exit 1
    ;;
esac
