package inclub9.intellij.plugin.fieldlabel

import com.intellij.psi.*
import com.intellij.psi.augment.PsiAugmentProvider
import org.jetbrains.annotations.NotNull

class FieldLabelContributor : PsiAugmentProvider() {

    @NotNull
    override fun <Psi : PsiElement> getAugments(
        @NotNull element: PsiElement,
        @NotNull type: Class<Psi>
    ): List<Psi> {
        if (element !is PsiClass) {
            return emptyList()
        }

        if (!PsiMethod::class.java.isAssignableFrom(type)) {
            return emptyList()
        }

        return element.fields
            .mapNotNull { field ->
                field.getAnnotation("inclub9.annotation.FieldLabel")?.let { annotation ->
                    createVirtualMethods(field, annotation)
                }
            }
            .flatten()
            .filterIsInstance(type)
    }

    private fun createVirtualMethods(field: PsiField, annotation: PsiAnnotation): List<PsiMethod> {
        val factory = JavaPsiFacade.getInstance(field.project).elementFactory
        val labelValue = getLabelValue(annotation)
        val capitalizedFieldName = field.name.capitalize()

        return listOf(
            // Constant getter method
            createLabelGetter(factory, field, capitalizedFieldName, labelValue),
            // Field name getter method
            createFieldNameGetter(factory, field, capitalizedFieldName)
        )
    }

    private fun createLabelGetter(
        factory: PsiElementFactory,
        field: PsiField,
        capitalizedFieldName: String,
        labelValue: String
    ): PsiMethod {
        val template = """
            public static String get${capitalizedFieldName}Label() {
                return "$labelValue";
            }
        """.trimIndent()
        return factory.createMethodFromText(template, field)
    }

    private fun createFieldNameGetter(
        factory: PsiElementFactory,
        field: PsiField,
        capitalizedFieldName: String
    ): PsiMethod {
        val template = """
            public static String get${capitalizedFieldName}FieldName() {
                return "${field.name}";
            }
        """.trimIndent()
        return factory.createMethodFromText(template, field)
    }

    private fun getLabelValue(annotation: PsiAnnotation): String {
        return annotation.findAttributeValue("value")?.let { value ->
            if (value is PsiLiteralExpression) {
                value.value?.toString() ?: ""
            } else ""
        } ?: ""
    }
}
