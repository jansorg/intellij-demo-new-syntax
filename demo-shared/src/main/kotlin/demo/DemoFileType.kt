package demo

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object DemoFileType : LanguageFileType(DemoLanguage) {
    override fun getName(): String = "Demo"

    override fun getDescription(): String = "Demo file"

    override fun getDefaultExtension(): String = "demo"

    override fun getIcon(): Icon = AllIcons.FileTypes.Text
}
