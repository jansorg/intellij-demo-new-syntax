package demo.parser

import com.intellij.platform.syntax.Logger
import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime

/** The root of a parsed Demo file. demo.shared maps it to the file element type of the PSI. */
val DEMO_FILE: SyntaxElementType = SyntaxElementType("DEMO_FILE")

/** Parses the tokens of [builder] as a Demo file with the generated [DemoSyntaxParser]. */
fun parseDemoFile(builder: SyntaxTreeBuilder, logger: Logger) {
    val runtime = SyntaxGeneratedParserRuntime(builder, null, KEYWORD_TEXT_IGNORE_CASE, emptyList(), logger, MAX_RECURSION_DEPTH)
    DemoSyntaxParser.parse(DEMO_FILE, runtime)
}

/**
 * The flag "isLanguageCaseSensitive" of the runtime, which it passes on as "ignoreCase" when it compares the text of a
 * keyword. `false` compares case-sensitively.
 */
private const val KEYWORD_TEXT_IGNORE_CASE = false

/** The recursion depth at which a generated parser stops, the default of Grammar-Kit. */
private const val MAX_RECURSION_DEPTH = 1000
