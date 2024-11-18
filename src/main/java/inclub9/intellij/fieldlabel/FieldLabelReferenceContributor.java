package inclub9.intellij.fieldlabel;

import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class FieldLabelReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PsiJavaPatterns.psiElement(PsiReferenceExpression.class),
                new PsiReferenceProvider() {
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                                           @NotNull ProcessingContext context) {
                        if (!(element instanceof PsiReferenceExpression)) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        PsiReferenceExpression refExpr = (PsiReferenceExpression) element;
                        PsiExpression qualifier = refExpr.getQualifierExpression();

                        if (!(qualifier instanceof PsiReferenceExpression)) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        String refText = qualifier.getText();
                        if (!refText.endsWith("Label")) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        return new PsiReference[]{new LabelFieldReference(refExpr)};
                    }
                });
    }

    private static class LabelFieldReference extends PsiReferenceBase<PsiReferenceExpression> {
        public LabelFieldReference(@NotNull PsiReferenceExpression element) {
            super(element);
        }

        @Override
        public PsiElement resolve() {
            PsiReferenceExpression refExpr = getElement();
            String fieldName = refExpr.getReferenceName();
            PsiExpression qualifier = refExpr.getQualifierExpression();

            if (!(qualifier instanceof PsiReferenceExpression)) {
                return null;
            }

            String className = qualifier.getText();
            if (!className.endsWith("Label")) {
                return null;
            }

            String originalClassName = className.substring(0, className.length() - 5);
            PsiClass psiClass = JavaPsiFacade.getInstance(refExpr.getProject())
                    .findClass(originalClassName, refExpr.getResolveScope());

            if (psiClass != null) {
                for (PsiField field : psiClass.getAllFields()) {
                    if (field.getName().equals(fieldName) ||
                            camelCaseToUpperUnderscore(field.getName()).equals(fieldName)) {
                        return field;
                    }
                }
            }

            return null;
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
}