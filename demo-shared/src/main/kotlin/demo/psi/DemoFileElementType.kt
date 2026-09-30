package demo.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.diagnostic.logger
import com.intellij.platform.syntax.psi.PsiSyntaxBuilderFactory
import com.intellij.platform.syntax.psi.asSyntaxLogger
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IFileElementType
import demo.DemoLanguage
import demo.lexer.DemoSyntaxLexer
import demo.parser.parseDemoFile

/** The root of a Demo file in the PSI. It parses with the generated parser of the syntax API. */
object DemoFileElementType : IFileElementType("DEMO_FILE", DemoLanguage) {
    private val LOG = logger<DemoFileElementType>()

    /** Returns `null` for an empty file, which has no children. */
    override fun doParseContents(chameleon: ASTNode, psi: PsiElement): ASTNode? {
        val builder = PsiSyntaxBuilderFactory.getInstance().createBuilder(chameleon, DemoSyntaxLexer(), DemoLanguage, chameleon.chars)
        parseDemoFile(builder.getSyntaxTreeBuilder(), LOG.asSyntaxLogger())
        return builder.getTreeBuilt().firstChildNode
    }
}
