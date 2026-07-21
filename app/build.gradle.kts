import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt)
    alias(libs.plugins.roborazzi)
    jacoco
}

jacoco { toolVersion = "0.8.12" }

// Load .env file if present
val envFile = rootProject.file(".env")
val envProps = Properties().apply {
    if (envFile.exists()) load(FileInputStream(envFile))
}

android {
    namespace = "com.example.template"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.template"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Inject API keys as BuildConfig fields (never hardcode)
        buildConfigField("String", "GEMINI_API_KEY",
            "\"${envProps["GEMINI_API_KEY"] ?: System.getenv("GEMINI_API_KEY") ?: ""}\"")
        buildConfigField("String", "GH_TOKEN",
            "\"${envProps["GH_TOKEN"] ?: System.getenv("GH_TOKEN") ?: ""}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**/*.kt")
        ktfmt().googleStyle()
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        ktfmt().googleStyle()
    }
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(files("$rootDir/config/detekt.yml"))
}

tasks.register<JacocoReport>("jacocoTestReportDebug") {
    dependsOn("testDebugUnitTest")
    group = "verification"
    description = "Generate JaCoCo coverage report for debug unit tests."

    val excludes = listOf(
        "**/R.class", "**/R\$*.class", "**/BuildConfig.*", "**/Manifest*.*",
        "**/*Dagger*.*", "**/*_Factory*.*",
    )

    val kotlinClasses =
        fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug") { exclude(excludes) }

    sourceDirectories.setFrom(files("${project.projectDir}/src/main/java"))
    classDirectories.setFrom(files(kotlinClasses))

    executionData.setFrom(
        fileTree(layout.buildDirectory.get()) {
            include(
                "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
                "jacoco/testDebugUnitTest.exec",
            )
        }
    )
}

dependencies {
    // ---------------------------------------------------------------------------
    // Dependency constraints: pin minimum safe versions for transitive deps
    // pulled in by Gradle toolchain, AGP, and build plugins.
    // These do NOT ship in the APK (build-time only) except where noted.
    // Uses Gradle dependency constraints (recognized by Dependabot).
    // ---------------------------------------------------------------------------
    constraints {
        // Netty — HTTP/2 DoS, request smuggling, hostname verification bypass
        add("implementation", "io.netty:netty-handler:4.1.135.Final") {
            because("CVE-2026-50010 hostname verification bypass")
        }
        add("implementation", "io.netty:netty-codec-http:4.1.135.Final") {
            because("CVE-2026-50020 control char bypass")
        }
        add("implementation", "io.netty:netty-codec-http2:4.1.135.Final") {
            because("CVE-2026-50560 HTTP/2 reset attack")
        }
        add("implementation", "io.netty:netty-handler-proxy:4.1.135.Final") {
            because("CVE-2026-33870 header injection")
        }
        add("implementation", "io.netty:netty-common:4.1.135.Final") {
            because("transitive pin for netty consistency")
        }
        add("implementation", "io.netty:netty-buffer:4.1.135.Final") {
            because("transitive pin for netty consistency")
        }
        add("implementation", "io.netty:netty-transport:4.1.135.Final") {
            because("transitive pin for netty consistency")
        }
        add("implementation", "io.netty:netty-codec:4.1.135.Final") {
            because("transitive pin for netty consistency")
        }
        add("implementation", "io.netty:netty-resolver:4.1.135.Final") {
            because("transitive pin for netty consistency")
        }

        // BouncyCastle — keystream reuse, LDAP injection, broken crypto
        add("implementation", "org.bouncycastle:bcprov-jdk18on:1.84") {
            because("CVE-2025-14813 GOST keystream reuse, GHSA-c3fc-8qff-9hwx LDAP injection")
        }
        add("implementation", "org.bouncycastle:bcpkix-jdk18on:1.84") {
            because("GHSA-wg6q-6289-32hp broken crypto algorithm")
        }

        // Okio — signed-to-unsigned (affects runtime APK via OkHttp)
        add("implementation", "com.squareup.okio:okio:3.4.0") {
            because("CVE-2023-3635 signed-to-unsigned conversion")
        }
        add("implementation", "com.squareup.okio:okio-jvm:3.4.0") {
            because("CVE-2023-3635 signed-to-unsigned conversion")
        }

        // Apache Commons — uncontrolled recursion, DoS
        add("implementation", "org.apache.commons:commons-lang3:3.18.0") {
            because("CVE-2025-48924 uncontrolled recursion")
        }
        add("implementation", "org.apache.commons:commons-compress:1.26.0") {
            because("CVE-2024-25710 infinite loop, CVE-2024-26308 OOM")
        }
        add("implementation", "commons-io:commons-io:2.14.0") {
            because("CVE-2024-47554 DoS via XmlStreamReader")
        }

        // Apache HttpClient — XSS
        add("implementation", "org.apache.httpcomponents:httpclient:4.5.14") {
            because("CVE-2020-13956 XSS")
        }

        // jose4j — DoS via compressed JWE
        add("implementation", "org.bitbucket.b_c:jose4j:0.9.6") {
            because("CVE-2023-51775 DoS via compressed JWE")
        }

        // Eclipse JGit — XXE vulnerability
        add("implementation", "org.eclipse.jgit:org.eclipse.jgit:6.10.1.202505221210-r") {
            because("GHSA-vrpq-qp53-qv56 XXE")
        }

        // protobuf-java — DoS
        add("implementation", "com.google.protobuf:protobuf-java:3.25.5") {
            because("CVE-2024-7254 DoS")
        }

        // JDOM2 — XXE vulnerability
        add("implementation", "org.jdom:jdom2:2.0.6.1") {
            because("GHSA-2363-cqg2-863c XXE")
        }
    }

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    "ksp"(libs.room.compiler)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.workmanager)
    implementation(libs.security.crypto)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi.core)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.compose.ui.test.junit4)
}

