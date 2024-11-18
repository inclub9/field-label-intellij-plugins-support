package inclub9.intellij.fieldlabel;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public class FieldLabelAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof PsiAnnotation annotation)) {
            return;
        }

        if (!"FieldLabel".equals(annotation.getQualifiedName())) {
            return;
        }

        PsiAnnotationMemberValue value = annotation.findAttributeValue("value");
        if (value == null) {
            return;
        }

        // Validate that the annotation value matches either the field name or its uppercase version
        PsiField field = (PsiField) annotation.getParent();
        String fieldName = field.getName();
        String upperCaseName = camelCaseToUpperUnderscore(fieldName);

        String annotationValue = value.getText().replace("\"", "");
        if (!fieldName.equals(annotationValue) && !upperCaseName.equals(annotationValue)) {
            holder.newAnnotation(HighlightSeverity.WARNING,
                            "Field label value doesn't match field name or its uppercase version")
                    .create();
        }
    }

    private String camelCaseToUpperUnderscore(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (i > 0 && Character.isUpperCase(c)) {
                result.append('_');
            }
            result.append(Character.toUpperCase(c));
        }
        return result.toString();
    }
}