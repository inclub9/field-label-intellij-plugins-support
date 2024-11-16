package inclub9.intellij.plugin.fieldlabel.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.util.PsiUtil
import inclub9.intellij.plugin.fieldlabel.util.LombokStyle

class FieldLabelAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is PsiField) return

        val annotation = element.annotations.find {
            it.qualifiedName == "inclub9.annotation.FieldLabel"
        } ?: return

        // Show the generated constant names in the editor
        val value = annotation.findAttributeValue("value")?.text?.trim('"')
        if (value != null) {
            val constantName = LombokStyle.camelCaseToUpperUnderscore(element.name)
            holder.newAnnotation(HighlightSeverity.INFORMATION,
                "Generates: $constantName = \"$value\"")
                .range(element.textRange)
                .create()
        }
    }
}
