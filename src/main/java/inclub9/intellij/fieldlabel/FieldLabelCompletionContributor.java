package inclub9.intellij.fieldlabel;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;

public class FieldLabelCompletionContributor extends CompletionContributor {

    public FieldLabelCompletionContributor() {
        extend(CompletionType.BASIC,
                PsiJavaPatterns.psiElement()
                        .afterLeaf(PsiJavaPatterns.psiElement().withText(".")),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        PsiElement element = parameters.getPosition();
                        PsiElement parent = element.getParent();

                        if (!(parent instanceof PsiReferenceExpression)) {
                            return;
                        }

                        PsiReferenceExpression refExpr = (PsiReferenceExpression) parent;
                        PsiExpression qualifier = refExpr.getQualifierExpression();

                        if (!(qualifier instanceof PsiReferenceExpression)) {
                            return;
                        }

                        String refText = qualifier.getText();
                        if (!refText.endsWith("Label")) {
                            return;
                        }

                        String originalClassName = refText.substring(0, refText.length() - 5);
                        Project project = element.getProject();
                        PsiManager psiManager = PsiManager.getInstance(project);

                        // ค้นหา class จากทุกไฟล์ Java ใน project
                        PsiClass targetClass = null;
                        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
                        PsiClass[] classes = JavaPsiFacade.getInstance(project)
                                .findClasses(originalClassName, scope);

                        if (classes.length > 0) {
                            targetClass = classes[0];
                        } else {
                            // ถ้าไม่เจอ ลองค้นหาจากชื่อไฟล์
                            String fileName = originalClassName + ".java";
                            @NotNull Collection<VirtualFile> files = FilenameIndex.getVirtualFilesByName(fileName, scope);
                            for (VirtualFile file : files) {
                                PsiFile psiFile = psiManager.findFile(file);
                                if (psiFile instanceof PsiJavaFile) {
                                    PsiJavaFile javaFile = (PsiJavaFile) psiFile;
                                    PsiClass[] fileClasses = javaFile.getClasses();
                                    for (PsiClass cls : fileClasses) {
                                        if (cls.getName().equals(originalClassName)) {
                                            targetClass = cls;
                                            break;
                                        }
                                    }
                                    if (targetClass != null) break;
                                }
                            }
                        }

                        if (targetClass != null) {
                            addLabelFields(targetClass, result);
                        }
                    }
                });
    }

    private void addLabelFields(PsiClass psiClass, CompletionResultSet result) {
        for (PsiField field : psiClass.getAllFields()) {
            PsiAnnotation annotation = field.getAnnotation("inclub9.annotation.FieldLabel");
            if (annotation != null) {
                PsiAnnotationMemberValue value = annotation.findAttributeValue("value");
                String labelValue = value != null ? value.getText().replace("\"", "") : "";

                // Add field name as a constant
                result.addElement(LookupElementBuilder.create(field.getName())
                        .withPresentableText(field.getName())
                        .withTypeText(labelValue)
                        .withTailText(" = \"" + labelValue + "\"")
                        .withIcon(field.getIcon(0))
                        .withBoldness(true));

                // Add uppercase version
                String upperCaseName = camelCaseToUpperUnderscore(field.getName());
                result.addElement(LookupElementBuilder.create(upperCaseName)
                        .withPresentableText(upperCaseName)
                        .withTypeText(labelValue)
                        .withTailText(" = \"" + labelValue + "\"")
                        .withIcon(field.getIcon(0))
                        .withBoldness(true));
            }
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