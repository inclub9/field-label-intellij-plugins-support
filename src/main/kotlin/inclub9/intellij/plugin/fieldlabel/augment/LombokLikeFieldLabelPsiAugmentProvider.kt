package inclub9.intellij.plugin.fieldlabel.augment

import com.intellij.psi.*
import com.intellij.psi.augment.PsiAugmentProvider
import com.intellij.psi.impl.light.LightFieldBuilder
import com.intellij.psi.impl.light.LightModifierList

class LombokLikeFieldLabelPsiAugmentProvider : PsiAugmentProvider() {
    override fun <Psi : PsiElement?> getAugments(
        element: PsiElement,
        type: Class<Psi>,
        nameHint: String?
    ): List<Psi> {
        // 1. ต้องเป็น PsiClass และต้องการ augment PsiField เท่านั้น
        if (element !is PsiClass || type != PsiField::class.java) {
            return emptyList()
        }

        val fields = mutableListOf<PsiField>()

        // 2. วนหา fields ที่มี @FieldLabel
        element.fields.forEach { field ->
            val annotation = field.annotations.find {
                it.qualifiedName == "inclub9.annotation.FieldLabel"
            } ?: return@forEach

            // 3. สร้าง virtual constant field
            val constantName = camelCaseToUpperUnderscore(field.name)
            val value = annotation.findAttributeValue("value")?.text?.trim('"') ?: return@forEach

            // 4. สร้าง Light PSI Field สำหรับ constant
            fields.add(createLightField(element, constantName, value))
        }

        @Suppress("UNCHECKED_CAST")
        return fields as List<Psi>
    }

    private fun createLightField(containingClass: PsiClass, fieldName: String, value: String): PsiField {
        val modifierList = LightModifierList(containingClass.manager).apply {
            addModifier(PsiModifier.PUBLIC)
            addModifier(PsiModifier.STATIC)
            addModifier(PsiModifier.FINAL)
        }

        val builder = LightFieldBuilder(
            containingClass.manager,
            fieldName,
            PsiType.getJavaLangString(containingClass.manager, containingClass.resolveScope)
        )

        builder.setContainingClass(containingClass) // แก้จาก containingClass = เป็น setContainingClass()
        builder.setModifierList(modifierList) // แก้จาก modifierList = เป็น setModifierList()

        // สร้าง initializer
        val psiElementFactory = PsiElementFactory.getInstance(containingClass.project)
        val initializer = psiElementFactory.createExpressionFromText("\"$value\"", builder)
        builder.initializer = initializer // อันนี้สามารถ assign ได้

        return builder
    }

    private fun camelCaseToUpperUnderscore(input: String): String {
        val result = StringBuilder()
        for (i in input.indices) {
            val c = input[i]
            if (i > 0 && Character.isUpperCase(c)) {
                result.append('_')
            }
            result.append(Character.toUpperCase(c))
        }
        return result.toString()
    }
}