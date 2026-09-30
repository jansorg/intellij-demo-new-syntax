package demo.psi

import com.intellij.platform.syntax.psi.ElementTypeConverter
import com.intellij.platform.syntax.psi.ElementTypeConverterFactory
import com.intellij.platform.syntax.psi.elementTypeConverterOf
import demo.parser.DEMO_FILE

/** Maps the root of the syntax tree to the file element type. The generated [DemoElementTypeConverterFactory] maps the rest. */
class DemoFileElementTypeConverterFactory : ElementTypeConverterFactory {
    override fun getElementTypeConverter(): ElementTypeConverter = elementTypeConverterOf(DEMO_FILE to DemoFileElementType)
}
