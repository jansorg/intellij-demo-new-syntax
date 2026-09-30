pluginManagement {
    // The Kotlin version must match the one the IDE is built with, it's configured per platform version,
    // see gradle-<platformVersion>.properties. This block is evaluated before anything else of the settings,
    // so it reads that file itself.
    val platformVersion = providers.gradleProperty("platformVersion").get()
    val propertiesFile = settingsDir.resolve("gradle-$platformVersion.properties")
    require(propertiesFile.isFile) { "Unsupported platformVersion '$platformVersion', $propertiesFile is missing." }

    val platform = java.util.Properties().apply {
        propertiesFile.inputStream().use { load(it) }
    }

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("jvm") version platform.getProperty("kotlinPluginVersion")
    }
}

plugins {
    // Downloads the JDK the IntelliJ Platform Gradle Plugin requests, if it's not installed locally
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "intellij-demo-new-syntax"

include(
    // lexer and parser of the syntax API, no PSI
    "demo-syntax",
    // language, file type, PSI, parser definition, highlighter
    "demo-shared",
    // features which need the backend, e.g. an annotator
    "demo-backend",
)
