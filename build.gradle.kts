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
// Dependency constraints: force minimum safe versions for transitive deps
// that are pulled in by Gradle toolchain, AGP, and build plugins.
// These do NOT ship in the APK (build-time only) except where noted.
// ---------------------------------------------------------------------------
allprojects {
    configurations.all {
        resolutionStrategy {
            // Netty — multiple HTTP/2 DoS, request smuggling, hostname verification
            force("io.netty:netty-handler:4.1.135.Final")
            force("io.netty:netty-codec-http:4.1.135.Final")
            force("io.netty:netty-codec-http2:4.1.135.Final")
            force("io.netty:netty-handler-proxy:4.1.135.Final")
            force("io.netty:netty-common:4.1.135.Final")
            force("io.netty:netty-buffer:4.1.135.Final")
            force("io.netty:netty-transport:4.1.135.Final")
            force("io.netty:netty-codec:4.1.135.Final")
            force("io.netty:netty-resolver:4.1.135.Final")

            // BouncyCastle — GOST keystream reuse, LDAP injection, broken crypto
            force("org.bouncycastle:bcprov-jdk18on:1.81.1")
            force("org.bouncycastle:bcpkix-jdk18on:1.84")

            // Okio — signed-to-unsigned conversion (affects runtime APK via OkHttp)
            force("com.squareup.okio:okio:3.4.0")
            force("com.squareup.okio:okio-jvm:3.4.0")

            // Apache Commons — uncontrolled recursion, DoS
            force("org.apache.commons:commons-lang3:3.18.0")
            force("org.apache.commons:commons-compress:1.26.0")
            force("commons-io:commons-io:2.14.0")

            // Apache HttpClient — XSS
            force("org.apache.httpcomponents:httpclient:4.5.14")

            // jose4j — DoS via compressed JWE
            force("org.bitbucket.b_c:jose4j:0.9.6")

            // Eclipse JGit — XXE vulnerability
            force("org.eclipse.jgit:org.eclipse.jgit:6.10.1.202505221210-r")

            // protobuf-java — DoS
            force("com.google.protobuf:protobuf-java:3.25.5")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

