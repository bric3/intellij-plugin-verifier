package com.jetbrains.pluginverifier.configurations

import com.intellij.structure.resolvers.Resolver
import com.jetbrains.pluginverifier.api.*
import com.jetbrains.pluginverifier.dependency.DependencyResolver
import com.jetbrains.pluginverifier.misc.closeLogged


data class CheckIdeParams(val ideDescriptor: IdeDescriptor,
                          val jdkDescriptor: JdkDescriptor,
                          val pluginsToCheck: List<PluginCoordinate>,
                          val excludedPlugins: List<PluginIdAndVersion>,
                          val pluginIdsToCheckExistingBuilds: List<String>,
                          val externalClassPath: Resolver,
                          val externalClassesPrefixes: List<String>,
                          val problemsFilter: ProblemsFilter,
                          val progress: Progress = DefaultProgress(),
                          val dependencyResolver: DependencyResolver? = null) : ConfigurationParams {
  override fun presentableText(): String = """Check IDE configuration parameters:
IDE to be checked: $ideDescriptor
JDK: $jdkDescriptor
Plugins to be checked: [${pluginsToCheck.joinToString()}]
Excluded plugins: [${excludedPlugins.joinToString()}]
"""

  override fun close() {
    ideDescriptor.closeLogged()
    externalClassPath.closeLogged()
  }

  override fun toString(): String = presentableText()
}