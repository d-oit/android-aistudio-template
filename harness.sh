#!/usr/bin/env bash
# ==============================================================================
# Android AI Studio Template — Unified Developer Workflow Harness
# ==============================================================================
# All local development commands run through this script.
# Direct ./gradlew invocations should NOT be used for standard tasks.
# ==============================================================================
set -euo pipefail

# Load .env if present
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
fi

COLOR_HEADER='\033[1;35m'
COLOR_INFO='\033[0;34m'
COLOR_SUCCESS='\033[1;32m'
COLOR_ERROR='\033[1;31m'
COLOR_RESET='\033[0m'

print_header() {
    echo -e "\n${COLOR_HEADER}=== $1 ===${COLOR_RESET}"
}

print_info() {
    echo -e "${COLOR_INFO}[INFO] $1${COLOR_RESET}"
}

print_success() {
    echo -e "${COLOR_SUCCESS}[SUCCESS] $1${COLOR_RESET}"
}

print_error() {
    echo -e "${COLOR_ERROR}[ERROR] $1${COLOR_RESET}" >&2
}

die() {
    print_error "$*"
    exit 1
}

run_gradle() {
    print_info "Running: gradle $*"
    gradle --stacktrace "$@"
}

log_step() {
    printf '\n==> %s\n' "$*"
}

show_help() {
    echo -e "${COLOR_HEADER}Android AI Studio Template — Harness Commands${COLOR_RESET}"
    echo -e "Usage: ./harness.sh [command]"
    echo -e ""
    echo -e "Available Commands:"
    echo -e "  ${COLOR_INFO}help${COLOR_RESET}         Show this usage information guide"
    echo -e "  ${COLOR_INFO}verify${COLOR_RESET}       Full gate: format-check -> lint -> test -> build"
    echo -e "  ${COLOR_INFO}check${COLOR_RESET}        Static gate: format-check -> lint -> test"
    echo -e "  ${COLOR_INFO}build${COLOR_RESET}        Build debug APK"
    echo -e "  ${COLOR_INFO}build-release${COLOR_RESET} Build release APK (requires signing env vars)"
    echo -e "  ${COLOR_INFO}test${COLOR_RESET}         Run full test suite (unit + Robolectric)"
    echo -e "  ${COLOR_INFO}unit${COLOR_RESET}         Run JVM unit tests only"
    echo -e "  ${COLOR_INFO}e2e${COLOR_RESET}          Run ONLY local high-fidelity JVM E2E tests"
    echo -e "  ${COLOR_INFO}lint${COLOR_RESET}         Android Lint"
    echo -e "  ${COLOR_INFO}detekt${COLOR_RESET}       Run Detekt static analysis"
    echo -e "  ${COLOR_INFO}format-check${COLOR_RESET} Spotless format check"
    echo -e "  ${COLOR_INFO}format${COLOR_RESET}       Apply Spotless code formatting"
    echo -e "  ${COLOR_INFO}coverage${COLOR_RESET}     Generate JaCoCo coverage report"
    echo -e "  ${COLOR_INFO}codacy${COLOR_RESET}       Upload coverage to Codacy"
    echo -e "  ${COLOR_INFO}clean${COLOR_RESET}        Clean build artifacts"
    echo -e "  ${COLOR_INFO}wrapper-check${COLOR_RESET} Validate gradle-wrapper.jar integrity"
    echo -e "  ${COLOR_INFO}setup-hooks${COLOR_RESET}  Install Git pre-push hooks"
    echo -e ""
}

if [ $# -lt 1 ]; then
    show_help
    exit 1
fi

COMMAND="$1"

case "$COMMAND" in
    help|--help|-h)
        show_help
        ;;
    format-check)
        print_header "Step: Format Check"
        run_gradle spotlessCheck || die "Format check failed. Run './harness.sh format' to fix."
        print_success "Format check passed."
        ;;
    format|format-apply)
        print_header "Step: Apply Code Formatting"
        run_gradle spotlessApply || die "Applying formatting failed."
        print_success "Code formatting applied successfully."
        ;;
    detekt)
        print_header "Step: Detekt Static Analysis"
        run_gradle detekt || die "Detekt analysis failed."
        print_success "Detekt static analysis passed."
        ;;
    lint)
        print_header "Step: Android Lint"
        run_gradle :app:lintDebug || die "Lint check failed."
        print_success "Lint check passed."
        ;;
    unit)
        print_header "Step: Unit Tests"
        run_gradle :app:testDebugUnitTest || die "Unit tests failed."
        print_success "All unit tests passed."
        ;;
    e2e)
        print_header "Step: E2E Test Execution"
        run_gradle :app:testDebugUnitTest --tests "*E2ETest" || die "E2E tests failed."
        print_success "All E2E tests passed."
        ;;
    test)
        print_header "Step: Full Test Pyramid"
        "$0" unit
        print_success "Test Pyramid passed."
        ;;
    build)
        print_header "Step: Build Debug APK"
        run_gradle assembleDebug || die "Debug build failed."
        print_success "Debug APK built."
        ;;
    build-release)
        print_header "Step: Build Release APK"
        run_gradle assembleRelease || die "Release build failed."
        print_success "Release APK built."
        ;;
    check)
        print_header "Step: Static Analysis Gate"
        "$0" format-check
        "$0" lint
        "$0" test
        print_success "Static analysis gate passed."
        ;;
    verify)
        print_header "Step: Full Verification Pipeline"
        "$0" format-check
        "$0" lint
        "$0" test
        "$0" build
        "$0" coverage
        print_success "Verification pipeline passed."
        ;;
    coverage)
        log_step "Generating JaCoCo coverage report"
        run_gradle jacocoTestReportDebug
        echo "HTML report: app/build/reports/jacoco/jacocoTestReportDebug/html/index.html"
        if [ -n "${CODACY_PROJECT_TOKEN:-}" ]; then
            "$0" codacy
        fi
        ;;
    codacy)
        log_step "Uploading coverage to Codacy"
        if [ -z "${CODACY_PROJECT_TOKEN:-}" ]; then
            echo "CODACY_PROJECT_TOKEN not set, skipping."
            exit 0
        fi
        REPORT="app/build/reports/jacoco/jacocoTestReportDebug/jacocoTestReportDebug.xml"
        if [ ! -f "$REPORT" ]; then
            die "Coverage report not found. Run './harness.sh coverage' first."
        fi
        bash <(curl -Ls https://coverage.codacy.com/get.sh) report -r "$REPORT"
        ;;
    clean)
        print_header "Step: Gradle Clean"
        run_gradle clean || die "Gradle clean failed."
        print_success "Clean completed."
        ;;
    wrapper-check)
        print_header "Step: Gradle Wrapper Integrity"
        WRAPPER_JAR="gradle/wrapper/gradle-wrapper.jar"
        [ -f "$WRAPPER_JAR" ] || die "Wrapper jar missing."
        if ! (unzip -t "$WRAPPER_JAR" >/dev/null 2>&1); then
            die "Wrapper jar is corrupt."
        fi
        ACTUAL_BYTES=$(wc -c < "$WRAPPER_JAR")
        if [ "$ACTUAL_BYTES" -lt 35000 ]; then
            die "Wrapper jar suspiciously small (${ACTUAL_BYTES} bytes)."
        fi
        print_success "Wrapper integrity OK (${ACTUAL_BYTES} bytes)."
        ;;
    setup-hooks)
        print_header "Step: Install Git Hooks"
        if [ ! -d ".git" ]; then
            die "Not a git repository."
        fi
        HOOK_FILE=".git/hooks/pre-push"
        cat << 'EOF' > "$HOOK_FILE"
#!/usr/bin/env bash
set -euo pipefail
echo "Running pre-push verification..."
if ! ./harness.sh check; then
    echo "Verification failed! Push aborted."
    exit 1
fi
echo "Verification passed."
EOF
        chmod +x "$HOOK_FILE"
        print_success "Pre-push hook installed."
        ;;
    *)
        print_error "Unknown command: '$COMMAND'"
        show_help
        exit 1
        ;;
esac
