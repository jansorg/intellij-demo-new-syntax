package demo.build

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.CommandLineArgumentProvider
import java.io.File

/**
 * Generates a part of the parser of a language from its Grammar-Kit grammar, see [part].
 *
 * Grammar-Kit ships a command line of its own, which always writes a parser of the classic PSI API.
 * A parser of the platform syntax API is only written by its generator service, so this task runs
 * [launcher] instead, a Java source file that calls that service. Java compiles and runs a source file
 * in one go, see JEP 330, so the launcher needs no build of its own and still reads the classes of
 * Grammar-Kit and of the IDE, which are on the classpath of this task.
 *
 * One generation writes the parser and the PSI of a language, which belong to different modules:
 * the syntax module holds the parser and the element types of the syntax API, which know nothing of
 * the PSI, and the shared module holds the PSI. Each module runs this task for its own [part].
 * The syntax part are the files of the grammar's `parserClass` and `syntaxElementTypeHolderClass`,
 * everything else is the PSI.
 */
@CacheableTask
abstract class GenerateParserTask : JavaExec() {
    /** The grammar to read. */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val grammar: RegularFileProperty

    /** The source file that drives Grammar-Kit. */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val launcher: RegularFileProperty

    /** The part of the generated sources to keep. */
    @get:Input
    abstract val part: Property<GeneratedPart>

    /** Holds the generated sources of [part]. */
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    init {
        mainClass.set(launcher.map { it.asFile.absolutePath })

        argumentProviders.add(
            CommandLineArgumentProvider {
                listOf(generationDirectory().absolutePath, grammar.get().asFile.absolutePath)
            }
        )
    }

    @TaskAction
    override fun exec() {
        val generation = generationDirectory()
        val output = outputDirectory.get().asFile

        // Grammar-Kit leaves the file of a rule behind that was renamed or dropped.
        listOf(generation, output).forEach { directory ->
            directory.deleteRecursively()
            directory.mkdirs()
        }

        super.exec()

        keepPart(generation, output)
    }

    /** The directory Grammar-Kit writes all sources to, before the ones of [part] are kept. */
    private fun generationDirectory(): File = temporaryDir.resolve("generated")

    /** Copies the sources of [part] from [generation] to [output]. */
    private fun keepPart(generation: File, output: File) {
        val grammarText = grammar.get().asFile.readText()
        val syntaxFiles = SYNTAX_CLASS_ATTRIBUTES
            .mapNotNull { attribute -> attributeValue(grammarText, attribute) }
            .map { className -> className.replace('.', '/') }
            .toSet()

        generation.walkTopDown().filter(File::isFile).forEach { file ->
            val relativePath = file.relativeTo(generation).invariantSeparatorsPath
            val isSyntaxFile = relativePath.substringBeforeLast('.') in syntaxFiles
            val keep = when (part.get()) {
                GeneratedPart.SYNTAX -> isSyntaxFile
                GeneratedPart.PSI -> !isSyntaxFile
            }

            if (keep) {
                val target = output.resolve(relativePath)
                file.copyTo(target, overwrite = true)
                if (target.extension == "kt") {
                    target.writeText(fixGeneratedKotlin(target.readText()))
                }
            }
        }
    }

    /**
     * Grammar-Kit passes `null` as the extends sets of a grammar without any `extends` relation, which the runtime of the
     * syntax API does not accept. An empty array means the same.
     */
    private fun fixGeneratedKotlin(source: String): String {
        return source.replace("runtime_.init(::parse, null)", "runtime_.init(::parse, emptyArray())")
    }

    private fun attributeValue(grammarText: String, attribute: String): String? {
        return Regex("""\b$attribute\s*=\s*"([^"]+)"""").find(grammarText)?.groupValues?.get(1)
    }

    private companion object {
        /** The attributes of a grammar which name the classes of the syntax part. */
        val SYNTAX_CLASS_ATTRIBUTES = listOf("parserClass", "syntaxElementTypeHolderClass")
    }
}

/** A part of the sources a grammar generates, which belongs to a module of its own. */
enum class GeneratedPart {
    /** The parser and the element types of the platform syntax API, which know nothing of the PSI. */
    SYNTAX,

    /** The PSI: its element types, the interface of every rule and the class that implements it. */
    PSI,
}
