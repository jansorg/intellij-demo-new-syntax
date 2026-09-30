package demo.build

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.language.base.plugins.LifecycleBasePlugin
import java.io.File

/**
 * Registers a task per JFlex grammar `*.flex` of [grammarDir], which generates a Kotlin lexer of the syntax API
 * into `build/generated/lexer/<language>`.
 */
fun Project.registerLexerTasks(grammarDir: Directory): List<TaskProvider<GenerateLexerTask>> {
    // JetBrains' JFlex, which emits Kotlin
    repositories.maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")

    val jflex = configurations.create("jflex") {
        isCanBeConsumed = false
        isCanBeResolved = true
    }
    dependencies.add(jflex.name, "org.jetbrains.intellij.deps.jflex:jflex:${providers.gradleProperty("jflexVersion").get()}")

    return grammarsOf(grammarDir.asFile, "flex").map { grammar ->
        val language = grammar.nameWithoutExtension

        tasks.register<GenerateLexerTask>("generate${language.replaceFirstChar(Char::titlecase)}Lexer") {
            group = LifecycleBasePlugin.BUILD_GROUP
            description = "Generates the $language lexer from ${grammar.name}"

            this.grammar.set(grammarDir.file(grammar.name))
            skeleton.set(rootProject.layout.projectDirectory.file("tools/lexer/idea-flex-kotlin.skeleton"))
            jflexClasspath.from(jflex)
            // one directory per grammar, lexers of a module must not overwrite each other
            outputDirectory.set(layout.buildDirectory.dir("generated/lexer/$language"))
        }
    }
}

/**
 * Registers a task per Grammar-Kit grammar `*.bnf` of [grammarDir], which generates [part] of the parser
 * into `build/generated/<part>/<language>`.
 *
 * The module must apply the plugin `org.jetbrains.intellij.platform.grammarkit` and declare the dependency
 * `intellijPlatform { grammarKit(...) }`. The plugin's own tasks are disabled, but its task `generateParser` still
 * provides the classpath of Grammar-Kit and the IDE, which [GenerateParserTask] runs its launcher with.
 */
fun Project.registerGrammarKitTasks(grammarDir: Directory, part: GeneratedPart): List<TaskProvider<GenerateParserTask>> {
    val stockParserTask = tasks.named<JavaExec>("generateParser")
    tasks.named { it == "generateParser" || it == "generateLexer" }.configureEach { enabled = false }

    val taskSuffix = part.name.lowercase().replaceFirstChar(Char::titlecase)
    return grammarsOf(grammarDir.asFile, "bnf").map { grammar ->
        val language = grammar.nameWithoutExtension

        tasks.register<GenerateParserTask>("generate${language.replaceFirstChar(Char::titlecase)}$taskSuffix") {
            group = LifecycleBasePlugin.BUILD_GROUP
            description = "Generates the ${part.name.lowercase()} part of the $language parser from ${grammar.name}"

            this.grammar.set(grammar)
            this.part.set(part)
            launcher.set(rootProject.layout.projectDirectory.file("tools/parser/GrammarKitGenerator.java"))
            classpath(stockParserTask.map { it.classpath })
            // one directory per grammar, grammars of a module must not overwrite each other
            outputDirectory.set(layout.buildDirectory.dir("generated/${part.name.lowercase()}/$language"))
        }
    }
}

private fun grammarsOf(grammarDir: File, extension: String): List<File> {
    return grammarDir.listFiles { file -> file.isFile && file.extension == extension }.orEmpty().sorted()
}
