package demo.lexer

import com.intellij.platform.syntax.util.lexer.FlexAdapter

/** The lexer of Demo files, driven by the generated [_DemoLexer]. */
class DemoSyntaxLexer : FlexAdapter(_DemoLexer())
