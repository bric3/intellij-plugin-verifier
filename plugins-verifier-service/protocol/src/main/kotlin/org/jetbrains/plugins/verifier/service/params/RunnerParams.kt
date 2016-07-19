package org.jetbrains.plugins.verifier.service.params

import com.google.common.collect.Multimap
import com.google.gson.annotations.SerializedName
import com.intellij.structure.domain.IdeVersion
import com.jetbrains.pluginverifier.api.VOptions

enum class JdkVersion {
  JAVA_6_ORACLE,
  JAVA_7_ORACLE,
  JAVA_8_ORACLE
}

data class CheckIdeRunnerParams(@SerializedName("jdkVersion") val jdkVersion: JdkVersion,
                                @SerializedName("vOptions") val vOptions: VOptions,
                                @SerializedName("checkAllBuilds") val checkAllBuilds: List<String>,
                                @SerializedName("checkLastBuilds") val checkLastBuilds: List<String>,
                                @SerializedName("excludedPlugins") val excludedPlugins: Multimap<String, String>,
                                @SerializedName("actualIdeVersion") val actualIdeVersion: IdeVersion? = null)

data class CheckPluginAgainstSinceUntilBuildsRunnerParams(@SerializedName("jdkVersion") val jdkVersion: JdkVersion,
                                                          @SerializedName("vOptions") val vOptions: VOptions)

data class CheckPluginRunnerParams(@SerializedName("jdkVersion") val jdkVersion: JdkVersion,
                                   @SerializedName("vOptions") val vOptions: VOptions)