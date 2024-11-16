package inclub9.intellij.plugin.fieldlabel.util

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.parentOfType

fun findGeneratedLabelClass(project: Project, element: PsiElement): PsiClass? {
    val containingClass = element.parentOfType<PsiClass>() ?: return null

    val labelClassName = "${containingClass.name}Label"
    val packageName = containingClass.qualifiedName?.substringBeforeLast('.') ?: return null
    val fullClassName = "$packageName.$labelClassName"

    return JavaPsiFacade.getInstance(project)
        .findClass(fullClassName, GlobalSearchScope.allScope(project))
}