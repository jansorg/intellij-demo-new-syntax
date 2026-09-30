package demo

import com.intellij.platform.syntax.psi.ElementTypeConverters
import com.intellij.platform.syntax.psi.lexer.LexerAdapter
import demo.lexer.DemoSyntaxLexer

/** The lexer of the PSI, which hands on the tokens of [DemoSyntaxLexer] as element types of the PSI. */
class DemoLexer : LexerAdapter(DemoSyntaxLexer(), ElementTypeConverters.getConverter(DemoLanguage))
