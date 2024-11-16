package inclub9.intellij.plugin.fieldlabel.augment

import com.intellij.psi.*
import com.intellij.psi.augment.PsiAugmentProvider
import inclub9.intellij.plugin.fieldlabel.util.LombokStyle

class FieldLabelPsiAugmentProvider : PsiAugmentProvider() {
    override fun <Psi : PsiElement?> getAugments(
        element: PsiElement,
        type: Class<Psi>,
        nameHint: String?
    ): List<Psi> {
        if (element !is PsiClass || type != PsiField::class.java) {
            return emptyList()
        }

        @Suppress("UNCHECKED_CAST")
        return LombokStyle.augmentClass(element) as List<Psi>
    }
}
