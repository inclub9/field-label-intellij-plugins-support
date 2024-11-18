package inclub9.intellij.fieldlabel.inspection;

import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public class FieldLabelInspection extends AbstractBaseJavaLocalInspectionTool {
    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder,
                                                   boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitAnnotation(@NotNull PsiAnnotation annotation) {
                if (!"FieldLabel".equals(annotation.getQualifiedName())) {
                    return;
                }

                PsiAnnotationMemberValue value = annotation.findAttributeValue("value");
                if (value == null) {
                    holder.registerProblem(annotation,
                            "FieldLabel annotation must have a value",
                            ProblemHighlightType.ERROR);
                    return;
                }

                if (!(annotation.getParent() instanceof PsiField)) {
                    holder.registerProblem(annotation,
                            "FieldLabel annotation can only be applied to fields",
                            ProblemHighlightType.ERROR);
                }
            }
        };
    }
}