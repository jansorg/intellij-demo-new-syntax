package demo

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import demo.psi.DemoFile
import demo.psi.DemoFileElementType
import demo.psi.DemoTypes

class DemoParserDefinition : ParserDefinition {
    override fun createLexer(project: Project?) = DemoLexer()

    /** Unused, [DemoFileElementType] parses with the parser of the syntax API. */
    override fun createParser(project: Project?): PsiParser = throw UnsupportedOperationException()

    override fun getFileNodeType() = DemoFileElementType

    override fun getCommentTokens(): TokenSet = TokenSet.EMPTY

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createElement(node: ASTNode): PsiElement = DemoTypes.Factory.createElement(node)

    override fun createFile(viewProvider: FileViewProvider) = DemoFile(viewProvider)
}
