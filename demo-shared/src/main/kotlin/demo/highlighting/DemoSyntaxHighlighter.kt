package demo.highlighting

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import demo.DemoLexer
import demo.psi.DemoTypes

class DemoSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer() = DemoLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> = when (tokenType) {
        DemoTypes.WORD -> arrayOf(DefaultLanguageHighlighterColors.IDENTIFIER)
        DemoTypes.EQ -> arrayOf(DefaultLanguageHighlighterColors.OPERATION_SIGN)
        else -> TextAttributesKey.EMPTY_ARRAY
    }
}
