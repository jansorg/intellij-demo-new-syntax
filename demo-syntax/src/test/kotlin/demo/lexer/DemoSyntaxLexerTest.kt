package demo.lexer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/** The lexer of the syntax API needs neither an application nor a project. */
class DemoSyntaxLexerTest {
    @Test
    fun tokens() {
        val text = "name = value\nkey=x"
        val lexer = DemoSyntaxLexer()
        lexer.start(text)

        val tokens = buildList {
            while (lexer.getTokenType() != null) {
                add("${lexer.getTokenType()} '${text.substring(lexer.getTokenStart(), lexer.getTokenEnd())}'")
                lexer.advance()
            }
        }

        assertEquals(
            listOf(
                "WORD 'name'", "WHITE_SPACE ' '", "= '='", "WHITE_SPACE ' '", "WORD 'value'", "WHITE_SPACE '\n'",
                "WORD 'key'", "= '='", "WORD 'x'",
            ),
            tokens,
        )
    }
}
