package inclub9.intellij.plugin.fieldlabel.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiClass
import com.intellij.psi.util.PsiUtil
import com.intellij.util.ProcessingContext
import inclub9.intellij.plugin.fieldlabel.util.LombokStyle

class FieldLabelCompletionContributor : CompletionContributor() {
    init {
        extend(CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    val position = parameters.position
                    val containingClass = PsiUtil.getTopLevelClass(position)

                    if (containingClass != null) {
                        // Add completion for generated constants directly from the augmented class
                        val augmentedFields = LombokStyle.augmentClass(containingClass)
                        augmentedFields.forEach { field ->
                            result.addElement(
                                LookupElementBuilder.create(field.name)
                                    .withTypeText("String")
                                    .withPresentableText(field.name)
                            )
                        }
                    }
                }
            }
        )
    }
}
