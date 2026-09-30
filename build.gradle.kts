import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.tasks.ComposedJarTask
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.util.Properties

plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij.platform") version "2.19.0"
    // Provides the classpath of Grammar-Kit to the parser generation of the modules, see buildSrc/Grammars.kt
    id("org.jetbrains.intellij.platform.grammarkit") version "2.19.0" apply false
}

/** The properties of the IntelliJ Platform version to build against, see gradle-<platformVersion>.properties. */
val platformVersion = providers.gradleProperty("platformVersion").get().toInt()
val platform = Properties().apply {
    rootProject.file("gradle-$platformVersion.properties").inputStream().use { load(it) }
}

allprojects {
    group = "dev.j-a.demo"
    version = "${providers.gradleProperty("pluginVersion").get()}-$platformVersion"

    repositories {
        mavenCentral()
    }

    // Every project applies its plugins in its own build script, the root project the plugin
    // org.jetbrains.intellij.platform, which applies org.jetbrains.intellij.platform.module, too.
    pluginManager.withPlugin("org.jetbrains.intellij.platform.module") {
        repositories {
            intellijPlatform {
                defaultRepositories()
            }
        }

        dependencies {
            intellijPlatform {
                intellijIdea(platform.getProperty("ideVersion"))
            }
        }

        intellijPlatform {
            buildSearchableOptions = false
            instrumentCode = false
        }
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        kotlin {
            jvmToolchain(platform.getProperty("javaVersion").toInt())

            compilerOptions {
                apiVersion.set(KotlinVersion.fromVersion(platform.getProperty("kotlinApiVersion")))
                languageVersion.set(KotlinVersion.fromVersion(platform.getProperty("kotlinApiVersion")))
            }
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        systemProperty("java.awt.headless", "true")
    }
}

subprojects {
    // The IDE loads a content module from lib/modules/<module name>.jar, e.g. demo.syntax of demo-syntax,
    // but the plugin names the jar <root project>.<project>.jar
    tasks.withType<ComposedJarTask>().configureEach {
        archiveFileName = "${project.name.replace('-', '.')}.jar"
    }
}

dependencies {
    intellijPlatform {
        // The content modules of the plugin, see <content> of plugin.xml
        pluginModule(implementation(project(":demo-syntax")))
        pluginModule(implementation(project(":demo-shared")))
        pluginModule(implementation(project(":demo-backend")))

        testFramework(TestFrameworkType.Platform)
        testFramework(TestFrameworkType.JUnit5)
    }

    testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // the test framework of the platform still refers to classes of JUnit 4
    testRuntimeOnly("junit:junit:4.13.2")
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = platform.getProperty("sinceBuild")
            untilBuild = platform.getProperty("untilBuild")
        }
    }
}

