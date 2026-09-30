package demo.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import demo.psi.DemoAssignment

/** Warns about a key which an assignment above already assigned. */
class DemoDuplicateKeyAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is DemoAssignment) return

        val key = element.key.text
        val first = PsiTreeUtil.getChildrenOfTypeAsList(element.parent, DemoAssignment::class.java).first { it.key.text == key }
        if (first != element) {
            holder.newAnnotation(HighlightSeverity.WARNING, "Duplicate key '$key'").range(element.key).create()
        }
    }
}
