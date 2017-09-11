package com.jetbrains.plugin.structure.teamcity

import com.jetbrains.plugin.structure.base.plugin.PluginProblem
import com.jetbrains.plugin.structure.base.problems.PropertyNotSpecified
import com.jetbrains.plugin.structure.teamcity.beans.TeamcityPluginBean

internal fun validateTeamcityPluginBean(bean: TeamcityPluginBean): List<PluginProblem> {
  val problems = arrayListOf<PluginProblem>()
  if (bean.name.isNullOrBlank()) {
    problems.add(PropertyNotSpecified("name"))
  }
  if (bean.displayName.isNullOrBlank()) {
    problems.add(PropertyNotSpecified("display-name"))
  }
  if (bean.version.isNullOrBlank()) {
    problems.add(PropertyNotSpecified("version"))
  }
  return problems
}
