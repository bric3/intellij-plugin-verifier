package com.jetbrains.pluginverifier.logging

import com.jetbrains.plugin.structure.intellij.version.IdeVersion
import com.jetbrains.pluginverifier.logging.loggers.Logger
import com.jetbrains.pluginverifier.parameters.ide.IdeDescriptor
import com.jetbrains.pluginverifier.plugin.PluginCoordinate
import com.jetbrains.pluginverifier.results.Verdict

class VerificationLoggerImpl(private val logger: Logger) : VerificationLogger {

  @Volatile
  private var verified: Int = 0

  private var tasksNumber: Int = 0

  internal fun pluginFinished(pluginInfo: PluginCoordinate, ideVersion: IdeVersion, verdict: Verdict) {
    ++verified
    logEvent("$verified of $tasksNumber finished. Plugin $pluginInfo and $ideVersion: $verdict")
  }

  @Synchronized
  override fun createPluginLogger(pluginCoordinate: PluginCoordinate, ideDescriptor: IdeDescriptor): PluginLogger {
    tasksNumber++
    val subLogger = logger.createSubLogger(pluginCoordinate.uniqueId)
    return PluginLoggerImpl(this, pluginCoordinate, ideDescriptor.ideVersion, subLogger)
  }

  override fun logEvent(message: String) {
    logger.info(message)
  }
}