// Top-level build file
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.detekt) apply false
}

// ---------------------------------------------------------------------------
// Dependency constraints: pin minimum safe versions for transitive deps
// pulled in by Gradle toolchain, AGP, and build plugins.
// These do NOT ship in the APK (build-time only) except where noted.
// Uses Gradle dependency constraints (recognized by Dependabot).
// ---------------------------------------------------------------------------
subprojects {
    dependencies {
        constraints {
            // Netty — HTTP/2 DoS, request smuggling, hostname verification bypass
            add("runtimeClasspath", "io.netty:netty-handler:4.1.135.Final") {
                because("CVE-2026-50010 hostname verification bypass")
            }
            add("runtimeClasspath", "io.netty:netty-codec-http:4.1.135.Final") {
                because("CVE-2026-50020 control char bypass")
            }
            add("runtimeClasspath", "io.netty:netty-codec-http2:4.1.135.Final") {
                because("CVE-2026-50560 HTTP/2 reset attack")
            }
            add("runtimeClasspath", "io.netty:netty-handler-proxy:4.1.135.Final") {
                because("CVE-2026-33870 header injection")
            }
            add("runtimeClasspath", "io.netty:netty-common:4.1.135.Final") {
                because("transitive pin for netty consistency")
            }
            add("runtimeClasspath", "io.netty:netty-buffer:4.1.135.Final") {
                because("transitive pin for netty consistency")
            }
            add("runtimeClasspath", "io.netty:netty-transport:4.1.135.Final") {
                because("transitive pin for netty consistency")
            }
            add("runtimeClasspath", "io.netty:netty-codec:4.1.135.Final") {
                because("transitive pin for netty consistency")
            }
            add("runtimeClasspath", "io.netty:netty-resolver:4.1.135.Final") {
                because("transitive pin for netty consistency")
            }

            // BouncyCastle — keystream reuse, LDAP injection, broken crypto
            add("runtimeClasspath", "org.bouncycastle:bcprov-jdk18on:1.84") {
                because("CVE-2025-14813 GOST keystream reuse, GHSA-c3fc-8qff-9hwx LDAP injection")
            }
            add("runtimeClasspath", "org.bouncycastle:bcpkix-jdk18on:1.84") {
                because("GHSA-wg6q-6289-32hp broken crypto algorithm")
            }

            // Okio — signed-to-unsigned (affects runtime APK via OkHttp)
            add("runtimeClasspath", "com.squareup.okio:okio:3.4.0") {
                because("CVE-2023-3635 signed-to-unsigned conversion")
            }
            add("runtimeClasspath", "com.squareup.okio:okio-jvm:3.4.0") {
                because("CVE-2023-3635 signed-to-unsigned conversion")
            }

            // Apache Commons — uncontrolled recursion, DoS
            add("runtimeClasspath", "org.apache.commons:commons-lang3:3.18.0") {
                because("CVE-2025-48924 uncontrolled recursion")
            }
            add("runtimeClasspath", "org.apache.commons:commons-compress:1.26.0") {
                because("CVE-2024-25710 infinite loop, CVE-2024-26308 OOM")
            }
            add("runtimeClasspath", "commons-io:commons-io:2.14.0") {
                because("CVE-2024-47554 DoS via XmlStreamReader")
            }

            // Apache HttpClient — XSS
            add("runtimeClasspath", "org.apache.httpcomponents:httpclient:4.5.14") {
                because("CVE-2020-13956 XSS")
            }

            // jose4j — DoS via compressed JWE
            add("runtimeClasspath", "org.bitbucket.b_c:jose4j:0.9.6") {
                because("CVE-2023-51775 DoS via compressed JWE")
            }

            // Eclipse JGit — XXE vulnerability
            add("runtimeClasspath", "org.eclipse.jgit:org.eclipse.jgit:6.10.1.202505221210-r") {
                because("GHSA-vrpq-qp53-qv56 XXE")
            }

            // protobuf-java — DoS
            add("runtimeClasspath", "com.google.protobuf:protobuf-java:3.25.5") {
                because("CVE-2024-7254 DoS")
            }

            // JDOM2 — XXE vulnerability
            add("runtimeClasspath", "org.jdom:jdom2:2.0.6.1") {
                because("GHSA-2363-cqg2-863c XXE")
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

