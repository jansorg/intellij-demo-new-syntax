package demo.build

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Generates a lexer from a JFlex grammar.
 *
 * The JFlex of JetBrains emits Kotlin, which the skeleton of the IntelliJ Platform shapes into a
 * lexer of the platform syntax API. JFlex runs in a JVM of its own, because it reports a faulty
 * grammar by ending the process.
 */
@CacheableTask
abstract class GenerateLexerTask : DefaultTask() {
    /** The grammar to read. */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val grammar: RegularFileProperty

    /** The skeleton that shapes the generated lexer. */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val skeleton: RegularFileProperty

    /** The JFlex distribution to run. */
    @get:Classpath
    abstract val jflexClasspath: ConfigurableFileCollection

    /** Holds the generated lexer. */
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Inject
    abstract val execOperations: ExecOperations

    @TaskAction
    fun generate() {
        val output = outputDirectory.get().asFile
        // JFlex leaves a stale lexer behind when a grammar is renamed or removed.
        output.deleteRecursively()
        output.mkdirs()

        execOperations.javaexec {
            classpath(jflexClasspath)
            mainClass.set("jflex.Main")
            args(
                "--output-mode", "kotlin",
                "--skel", skeleton.get().asFile.absolutePath,
                "-d", output.absolutePath,
                "--nobak",
                "--quiet",
                grammar.get().asFile.absolutePath,
            )
        }
    }
}
