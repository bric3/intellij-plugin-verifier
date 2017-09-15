package com.jetbrains.pluginverifier.dependencies

import com.jetbrains.plugin.structure.intellij.version.IdeVersion
import com.jetbrains.pluginverifier.repository.PluginRepository

class LastCompatibleSelector(val ideVersion: IdeVersion, val pluginRepository: PluginRepository) : DependencySelector {
  override fun select(pluginId: String): DependencySelector.Result {
    val updateInfo = pluginRepository.getLastCompatibleUpdateOfPlugin(ideVersion, pluginId)
    if (updateInfo != null) {
      return DependencySelector.Result.Plugin(updateInfo)
    }
    return DependencySelector.Result.NotFound("Plugin $pluginId doesn't have a build compatible with $ideVersion")
  }
}