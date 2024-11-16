package inclub9.intellij.plugin.fieldlabel.util

import com.intellij.psi.*
import com.intellij.psi.util.PsiUtil
import com.jetbrains.rd.util.ConcurrentHashMap

/**
 * Augments PsiClass with generated fields from FieldLabel annotations
 * Similar to how Lombok handles its generated members
 */
object LombokStyle {
    private val fieldCache = ConcurrentHashMap<String, List<PsiField>>()

    fun augmentClass(psiClass: PsiClass): List<PsiField> {
        val cacheKey = "${psiClass.qualifiedName}:${psiClass.modificationStamp}"

        return fieldCache.computeIfAbsent(cacheKey) {
            generateFields(psiClass)
        }
    }

    private fun generateFields(psiClass: PsiClass): List<PsiField> {
        val fields = mutableListOf<PsiField>()
        val factory = JavaPsiFacade.getElementFactory(psiClass.project)

        psiClass.fields.forEach { field ->
            field.getAnnotation("inclub9.annotation.FieldLabel")?.let { annotation ->
                val value = annotation.findAttributeValue("value")?.text?.trim('"') ?: return@let
                // สร้าง fields...
            }
        }

        return fields
    }

    // Clear cache เมื่อ class มีการเปลี่ยนแปลง
    fun clearCache(psiClass: PsiClass) {
        fieldCache.remove("${psiClass.qualifiedName}:${psiClass.modificationStamp}")
    }
}