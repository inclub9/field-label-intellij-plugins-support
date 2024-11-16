package inclub9.intellij.plugin.fieldlabel.inspection

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiField

// FieldLabelInspection.kt
class FieldLabelInspection : AbstractBaseJavaLocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitField(field: PsiField) {
                field.annotations.find { 
                    it.qualifiedName == "inclub9.annotation.FieldLabel" 
                }?.let { annotation ->
                    // ตรวจสอบความถูกต้องของ annotation
                    val value = annotation.findAttributeValue("value")
                    if (value == null || value.text.isNullOrBlank()) {
                        holder.registerProblem(
                            annotation,
                            "FieldLabel value must not be empty",
                            ProblemHighlightType.ERROR
                        )
                    }
                }
            }
        }
    }
}