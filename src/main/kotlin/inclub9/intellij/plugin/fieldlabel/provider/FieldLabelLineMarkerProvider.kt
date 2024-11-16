package inclub9.intellij.plugin.fieldlabel.provider

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.navigation.NavigationItem
import com.intellij.psi.PsiIdentifier
import inclub9.intellij.plugin.fieldlabel.icons.FieldLabelIcons
import inclub9.intellij.plugin.fieldlabel.util.LombokStyle
import inclub9.intellij.plugin.fieldlabel.util.findGeneratedLabelClass

class FieldLabelLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        // ต้องเช็คว่าเป็น identifier ของ field (leaf element)
        if (element !is PsiIdentifier || element.parent !is PsiField) return null

        val field = element.parent as PsiField
        val annotation = field.annotations.find {
            it.qualifiedName == "inclub9.annotation.FieldLabel"
        } ?: return null

        // แสดง icon ที่ identifier แทน field ทั้งก้อน
        return LineMarkerInfo(
            element,  // ใช้ identifier แทน field
            element.textRange,
            FieldLabelIcons.FIELD_LABEL,
            { "Navigate to generated label" },
            { _, elt ->
                val project = elt.project
                val psiClass = (elt.parent as? PsiField)?.containingClass
                if (psiClass != null) {
                    // ใช้ augmented fields แทนการหาไฟล์
                    val augmentedFields = LombokStyle.augmentClass(psiClass)
                    // แสดง popup หรือ navigate ไปยัง fields ที่จะถูกสร้าง
                }
            },
            GutterIconRenderer.Alignment.RIGHT,
            { "Generated FieldLabel constant" }
        )
    }
}