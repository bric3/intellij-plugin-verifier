/*
 * Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package com.jetbrains.plugin.structure.intellij.plugin

import com.jetbrains.plugin.structure.base.plugin.PluginIcon
import com.jetbrains.plugin.structure.base.plugin.ThirdPartyDependency
import com.jetbrains.plugin.structure.intellij.version.IdeVersion
import org.jdom2.Document
import org.jdom2.Element
import java.nio.file.Path

class IdePluginImpl : IdePlugin {
  override var pluginId: String? = null

  override var pluginName: String? = null

  override var pluginVersion: String? = null

  override var sinceBuild: IdeVersion? = null

  override var untilBuild: IdeVersion? = null

  override var originalFile: Path? = null

  override var productDescriptor: ProductDescriptor? = null

  override var vendor: String? = null

  override var vendorEmail: String? = null

  override var vendorUrl: String? = null

  override var description: String? = null

  override var changeNotes: String? = null

  override var url: String? = null

  override var useIdeClassLoader: Boolean = false

  override var isImplementationDetail: Boolean = false

  override var isV2: Boolean = false

  override var underlyingDocument: Document = Document()

  override val declaredThemes: MutableList<IdeTheme> = arrayListOf()

  override val definedModules: MutableSet<String> = hashSetOf()

  override val dependencies: MutableList<PluginDependency> = arrayListOf()

  override val incompatibleModules: MutableList<String> = arrayListOf()

  override val extensions: MutableMap<String, MutableList<Element>> = HashMap()

  val actions: MutableList<Element> = arrayListOf()

  override val appContainerDescriptor = MutableIdePluginContentDescriptor()

  override val projectContainerDescriptor = MutableIdePluginContentDescriptor()

  override val moduleContainerDescriptor = MutableIdePluginContentDescriptor()

  override var icons: List<PluginIcon> = emptyList()

  override val optionalDescriptors: MutableList<OptionalPluginDescriptor> = arrayListOf()

  override val modulesDescriptors: MutableList<ModuleDescriptor> = arrayListOf()

  override var thirdPartyDependencies: List<ThirdPartyDependency> = emptyList()

  override fun isCompatibleWithIde(ideVersion: IdeVersion) =
    (sinceBuild == null || sinceBuild!! <= ideVersion) && (untilBuild == null || ideVersion <= untilBuild!!)

  override fun toString(): String =
    (pluginId ?: pluginName ?: "<unknown plugin ID>") + (pluginVersion?.let { ":$it" } ?: "")
}
