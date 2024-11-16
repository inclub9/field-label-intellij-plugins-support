// FieldLabelCompletionContributor.kt
package inclub9.intellij.plugin.fieldlabel.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiClass
import com.intellij.psi.util.PsiUtil
import com.intellij.util.ProcessingContext
import inclub9.intellij.plugin.fieldlabel.util.findGeneratedLabelClass

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
                        // Add completion for generated label constants
                        addLabelCompletions(containingClass, result)
                    }
                }
            }
        )
    }

    private fun addLabelCompletions(psiClass: PsiClass, result: CompletionResultSet) {
        // Find generated label class and add its constants to completion
        val labelClass = findGeneratedLabelClass(psiClass.project, psiClass)
        if (labelClass != null) {
            for (field in labelClass.fields) {
                if (field.hasModifierProperty("public") && field.hasModifierProperty("static")) {
                    result.addElement(
                        LookupElementBuilder.create(field)
                            .withTypeText("FieldLabel")
                            .withIcon(AllIcons.Nodes.Field)
                    )
                }
            }
        }
    }
}
