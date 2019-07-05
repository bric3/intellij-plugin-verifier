package com.jetbrains.plugin.structure.base.problems

import com.jetbrains.plugin.structure.base.plugin.PluginProblem

abstract class PluginDescriptorResolutionError : PluginProblem() {
  override val level
    get() = Level.ERROR
}

class PluginDescriptorIsNotFound(private val descriptorPath: String) : PluginDescriptorResolutionError() {
  override val message
    get() = "Plugin descriptor '$descriptorPath' is not found"
}

class MultiplePluginDescriptors(
    private val firstDescriptorPath: String,
    private val firstDescriptorContainingFileName: String,
    private val secondDescriptorPath: String,
    private val secondDescriptorContainingFileName: String
) : PluginDescriptorResolutionError() {
  override val message: String
    get() {
      val firstIsLess = when {
        firstDescriptorPath < secondDescriptorPath -> true
        firstDescriptorPath == secondDescriptorPath -> firstDescriptorContainingFileName <= secondDescriptorContainingFileName
        else -> false
      }

      val (path1, file1) = if (firstIsLess) {
        firstDescriptorPath to firstDescriptorContainingFileName
      } else {
        secondDescriptorPath to secondDescriptorContainingFileName
      }

      val (path2, file2) = if (firstIsLess) {
        secondDescriptorPath to secondDescriptorContainingFileName
      } else {
        firstDescriptorPath to firstDescriptorContainingFileName
      }

      return "Found multiple plugin descriptors '$path1' from '$file1' and '$path2' from '$file2'"
    }
}
