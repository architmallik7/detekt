rootProject.name = "detekt"

pluginManagement {
    includeBuild("build-logic")
    includeBuild("detekt-gradle-plugin")
}

// import the plugin
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.harness:gradle-cache:0.0.2")
    }
}

// apply the plugin
apply(plugin = "io.harness.gradle-cache")

include("code-coverage-report")
include("detekt-api")
include("detekt-cli")
include("detekt-compiler-plugin")
include("detekt-core")
include("detekt-formatting")
include("detekt-generator")
include("detekt-metrics")
include("detekt-parser")
include("detekt-psi-utils")
include("detekt-report-html")
include("detekt-report-md")
include("detekt-report-sarif")
include("detekt-report-txt")
include("detekt-report-xml")
include("detekt-rules")
include("detekt-rules-complexity")
include("detekt-rules-coroutines")
include("detekt-rules-documentation")
include("detekt-rules-empty")
include("detekt-rules-errorprone")
include("detekt-rules-exceptions")
include("detekt-rules-libraries")
include("detekt-rules-naming")
include("detekt-rules-performance")
include("detekt-rules-ruleauthors")
include("detekt-rules-style")
include("detekt-sample-extensions")
include("detekt-test")
include("detekt-test-utils")
include("detekt-tooling")
include("detekt-utils")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

plugins {
    id("com.gradle.develocity") version "3.17.5"
    id("com.gradle.common-custom-user-data-gradle-plugin") version "2.0.2"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

val isCiBuild = providers.environmentVariable("CI").isPresent

develocity {
    buildScan {
        // Publish to scans.gradle.com when `--scan` is used explicitly
        if (!gradle.startParameter.isBuildScan) {
            server = "https://ge.detekt.dev"
            publishing.onlyIf { it.isAuthenticated }
        }

        uploadInBackground = !isCiBuild
    }
}

// Ensure buildCache config is kept in sync with all builds (root, build-logic & detekt-gradle-plugin)
buildCache {
    local {
        isEnabled = !isCiBuild
    }
    remote(develocity.buildCache) {
        server = "https://ge.detekt.dev"
        isEnabled = true
        val accessKey = System.getenv("DEVELOCITY_ACCESS_KEY")
        isPush = isCiBuild && !accessKey.isNullOrEmpty()
    }
}

// build cache config
buildCache {
    local {
        // Local build cache is dangerous as it might produce inconsistent results
        // in case developer modifies files while the build is running
        isEnabled = false
    }
    remote(io.harness.Cache::class.java) {
        accountId = System.getenv("HARNESS_ACCOUNT_ID") // accountId should be populated in CI pipeline
        token = System.getenv("HARNESS_PAT")            // API token with account admin (or edit) permissions
        isPush = true
        endpoint = System.getenv("HARNESS_CACHE_SERVICE_ENDPOINT") // https://app.harness.io/gateway/cache-service
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
