package inclub9.intellij.plugin.fieldlabel.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope

class FieldLabelAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is PsiField) return

        val annotations = element.annotations
        val fieldLabel = annotations.find { it.qualifiedName == "inclub9.annotation.FieldLabel" }

        if (fieldLabel != null) {
            val value = fieldLabel.findAttributeValue("value")?.text
            if (value != null) {
                holder.newAnnotation(HighlightSeverity.INFORMATION, "FieldLabel: $value")
                    .range(element.textRange)
                    .create()
            }
        }
    }
}