package com.jetbrains.pluginverifier.results.presentation

import com.jetbrains.plugin.structure.intellij.version.IdeVersion
import com.jetbrains.pluginverifier.misc.pluralize
import com.jetbrains.pluginverifier.results.hierarchy.ClassHierarchy
import com.jetbrains.pluginverifier.verifiers.logic.hierarchy.ClassHierarchyVisitor

object HierarchicalProblemsDescription {
  private fun findIdeSuperClassesAndInterfaces(ownerHierarchy: ClassHierarchy): Pair<Set<String>, Set<String>> {
    val ideSuperClasses = hashSetOf<String>()
    val ideSuperInterfaces = hashSetOf<String>()
    ClassHierarchyVisitor(true).visitClassHierarchy(ownerHierarchy, false, onEnter = { parent ->
      if (parent.isIdeClass) {
        if (parent.isInterface) {
          ideSuperInterfaces.add(parent.name)
        } else {
          ideSuperClasses.add(parent.name)
        }
      }
      true
    })
    return ideSuperClasses to ideSuperInterfaces
  }

  fun presentableElementMightHaveBeenDeclaredInIdeSuperTypes(
      elementType: String,
      ownerHierarchy: ClassHierarchy,
      ideVersion: IdeVersion,
      canBeDeclaredInSuperClass: Boolean,
      canBeDeclaredInSuperInterface: Boolean
  ): String {
    val (allSuperClasses, allSuperInterfaces) = findIdeSuperClassesAndInterfaces(ownerHierarchy)

    val superClasses = if (canBeDeclaredInSuperClass) allSuperClasses else emptySet()
    val superInterfaces = if (canBeDeclaredInSuperInterface) allSuperInterfaces else emptySet()

    return if (superClasses.isEmpty() && superInterfaces.isEmpty()) {
      ""
    } else buildString {
      append(" The $elementType might have been declared ")
      if (superClasses.isNotEmpty()) {
        append("in the super " + "class".pluralize(superClasses.size) + " belonging to $ideVersion")
        append(" (")
        append(superClasses.sorted().joinToString(transform = toFullJavaClassName))
        append(")")
      }
      if (superInterfaces.isNotEmpty()) {
        if (superClasses.isNotEmpty()) {
          append(" or ")
        }
        append("in the super " + "interface".pluralize(superInterfaces.size) + " belonging to $ideVersion")
        append(" (")
        append(superInterfaces.sorted().joinToString(transform = toFullJavaClassName))
        append(")")
      }
    }
  }
}