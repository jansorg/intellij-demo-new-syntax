package demo.parser

import com.intellij.openapi.application.readAction
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.impl.DebugUtil
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import demo.DemoLanguage
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/** Parses with the complete plugin, i.e. the parser definition and element type converters it registers. */
@TestApplication
class DemoParserTest {
    private val project = projectFixture()

    @Test
    fun parse() = runBlocking {
        val tree = readAction {
            val file = PsiFileFactory.getInstance(project.get()).createFileFromText("a.demo", DemoLanguage, "name = value\nkey =")
            DebugUtil.psiToString(file, false)
        }

        assertEquals(
            """
            DEMO_FILE
              DemoAssignmentImpl(ASSIGNMENT)
                DemoKeyImpl(KEY)
                  PsiElement(WORD)('name')
                PsiElement(=)('=')
                DemoValueImpl(VALUE)
                  PsiElement(WORD)('value')
              DemoAssignmentImpl(ASSIGNMENT)
                DemoKeyImpl(KEY)
                  PsiElement(WORD)('key')
                PsiElement(=)('=')
              PsiErrorElement:WORD expected
                <empty list>

            """.trimIndent(),
            tree,
        )
    }

    @Test
    fun parseEmptyFile() = runBlocking {
        val tree = readAction {
            val file = PsiFileFactory.getInstance(project.get()).createFileFromText("a.demo", DemoLanguage, "")
            DebugUtil.psiToString(file, false)
        }

        assertEquals("DEMO_FILE\n  <empty list>\n", tree)
    }
}
