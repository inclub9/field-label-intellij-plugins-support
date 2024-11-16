package inclub9.intellij.plugin.fieldlabel.util

import com.intellij.psi.*
import com.intellij.psi.util.PsiUtil

/**
 * Augments PsiClass with generated fields from FieldLabel annotations
 * Similar to how Lombok handles its generated members
 */
object LombokStyle {
    fun augmentClass(psiClass: PsiClass): List<PsiField> {
        val project = psiClass.project
        val factory = JavaPsiFacade.getElementFactory(project)
        val augmentedFields = mutableListOf<PsiField>()

        // Find all fields with @FieldLabel
        psiClass.fields.forEach { field ->
            field.annotations.find { it.qualifiedName == "inclub9.annotation.FieldLabel" }?.let { annotation ->
                val labelValue = annotation.findAttributeValue("value")?.text?.trim('"') ?: return@let

                // Generate constant field
                val constantName = camelCaseToUpperUnderscore(field.name)
                val constantField = factory.createFieldFromText(
                    "public static final String $constantName = \"$labelValue\";",
                    psiClass
                )
                augmentedFields.add(constantField)

                // Generate original field name constant
                val originalField = factory.createFieldFromText(
                    "public static final String ${field.name} = \"$labelValue\";",
                    psiClass
                )
                augmentedFields.add(originalField)
            }
        }

        return augmentedFields
    }

    fun camelCaseToUpperUnderscore(input: String): String {
        return input.replace(Regex("([a-z])([A-Z])"), "$1_$2").uppercase()
    }
}