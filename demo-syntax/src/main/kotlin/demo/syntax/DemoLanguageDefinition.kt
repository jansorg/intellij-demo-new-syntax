package demo.syntax

import com.intellij.platform.syntax.LanguageSyntaxDefinition
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.emptySyntaxElementTypeSet
import com.intellij.platform.syntax.lexer.Lexer
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import demo.lexer.DemoSyntaxLexer

/** What the platform needs to read Demo files with the syntax API. */
class DemoLanguageDefinition : LanguageSyntaxDefinition {
    override fun parse(builder: SyntaxTreeBuilder) {
        throw UnsupportedOperationException("A Demo file is parsed by its element type, which calls parseDemoFile.")
    }

    override fun createLexer(): Lexer = DemoSyntaxLexer()

    override val comments: SyntaxElementTypeSet = emptySyntaxElementTypeSet()
}
