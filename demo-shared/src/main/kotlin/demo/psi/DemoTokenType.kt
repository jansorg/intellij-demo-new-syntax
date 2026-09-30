package demo.psi

import com.intellij.psi.tree.IElementType
import demo.DemoLanguage

/** The element type of a token in the PSI, see `tokenTypeClass` of demo.bnf. */
class DemoTokenType(debugName: String) : IElementType(debugName, DemoLanguage)
