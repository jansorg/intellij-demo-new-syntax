package demo.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import demo.DemoFileType
import demo.DemoLanguage

class DemoFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, DemoLanguage) {
    override fun getFileType() = DemoFileType
}
