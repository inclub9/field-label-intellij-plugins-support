package inclub9.intellij.plugin.fieldlabel.inspection

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiField

class FieldLabelInspection : AbstractBaseJavaLocalInspectionTool() {
    
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitField(field: PsiField) {
                val annotation = field.getAnnotation("inclub9.annotation.FieldLabel")
                if (annotation != null) {
                    val value = annotation.findAttributeValue("value")
                    if (value == null || (value.text.length <= 2)) { // Empty or just quotes
                        holder.registerProblem(
                            annotation,
                            "FieldLabel annotation must have a non-empty value"
                        )
                    }
                }
            }
        }
    }
}
