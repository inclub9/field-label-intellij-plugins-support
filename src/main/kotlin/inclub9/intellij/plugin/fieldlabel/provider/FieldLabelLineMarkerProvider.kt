package inclub9.intellij.plugin.fieldlabel.provider

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.navigation.NavigationItem
import inclub9.intellij.plugin.fieldlabel.icons.FieldLabelIcons
import inclub9.intellij.plugin.fieldlabel.util.findGeneratedLabelClass

class FieldLabelLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is PsiField) return null

        val annotations = element.annotations
        val fieldLabel = annotations.find { it.qualifiedName == "inclub9.annotation.FieldLabel" }

        if (fieldLabel != null) {
            return LineMarkerInfo(
                element,
                element.textRange,
                FieldLabelIcons.FIELD_LABEL,
                { "Navigate to generated label class" },
                { _, elt ->
                    val project = elt.project
                    val generatedClass = findGeneratedLabelClass(project, elt)
                    if (generatedClass is NavigationItem) {
                        generatedClass.navigate(true)
                    }
                },
                GutterIconRenderer.Alignment.RIGHT,
                { "Generated FieldLabel" }
            )
        }
        return null
    }
}