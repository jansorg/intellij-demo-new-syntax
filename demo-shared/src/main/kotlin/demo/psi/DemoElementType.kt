package demo.psi

import com.intellij.psi.tree.IElementType
import demo.DemoLanguage

/** The element type of a rule of the grammar in the PSI, see `elementTypeClass` of demo.bnf. */
class DemoElementType(debugName: String) : IElementType(debugName, DemoLanguage)
