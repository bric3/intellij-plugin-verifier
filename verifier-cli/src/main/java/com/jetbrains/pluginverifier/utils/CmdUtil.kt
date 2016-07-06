package com.jetbrains.pluginverifier.utils

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.intellij.structure.domain.Ide
import com.intellij.structure.domain.IdeManager
import com.intellij.structure.domain.IdeVersion
import com.intellij.structure.resolvers.Resolver
import com.jetbrains.pluginverifier.api.VOptions
import com.sampullara.cli.Argument
import org.apache.commons.io.FileUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.regex.Pattern

open class CmdOpts(
    @set:Argument("runtime-dir", alias = "r", description = "The path to directory containing Java runtime jars (usually rt.jar and tools.jar are sufficient)")
    var runtimeDir: String? = null,

    @set:Argument("team-city", alias = "tc", description = "Specify this flag if you want to print the TeamCity compatible output.")
    var needTeamCityLog: Boolean = false,

    @set:Argument("tc-grouping", alias = "g", description = "How to group the TeamCity presentation of the problems")
    var group: String? = null,

    @set:Argument("ignored-problems", alias = "ip", description = "The problems specified in this file will be ignored. The file must contain lines in form <plugin_xml_id>:<plugin_version>:<problem_description_regexp_pattern>")
    var ignoreProblemsFile: String? = null,

    @set:Argument("plugins-to-check-all-builds", alias = "p-all", description = "The plugin ids to check with IDE. The plugin verifier will check ALL compatible plugin builds")
    var pluginToCheckAllBuilds: Array<String> = arrayOf(),

    @set:Argument("plugins-to-check-last-builds", alias = "p-last", description = "The plugin ids to check with IDE. The plugin verifier will check LAST plugin build only")
    var pluginToCheckLastBuild: Array<String> = arrayOf(),

    @set:Argument("ide-version", alias = "iv", description = "The actual version of the IDE that will be verified. This value will overwrite the one found in the IDE itself.")
    var actualIdeVersion: String? = null,

    @set:Argument("excluded-plugins-file", alias = "epf", description = "The file with list of excluded plugin builds (e.g. brokenPlugins.txt)")
    var excludedPluginsFile: String? = null,

    @set:Argument("dump-broken-plugin-list", alias = "d", description = "File to dump broken plugin list.")
    var dumpBrokenPluginsFile: String? = null,

    @set:Argument("html-report", description = "Create an HTML report of broken plugins")
    var htmlReportFile: String? = null,

    @set:Argument("results-file", description = "Save results to this file")
    var resultFile: String? = null,

    @set:Argument("plugins-to-check-file", alias = "ptcf", description = "The file that contains list of plugins to check (e.g. checkedPlugins.txt)")
    var pluginsToCheckFile: String? = null,

    @set:Argument("ignore-missing-optional-dependencies", alias = "imod", description = "Missing optional dependencies of the plugin IDs specified in this parameter will be ignored")
    var ignoreMissingOptionalDependencies: Array<String> = arrayOf(),

    @set:Argument("external-classpath", alias = "ex-cp", delimiter = ":", description = "The classes from external libraries. The Verifier will not report 'No such class' for such classes.")
    var externalClasspath: Array<String> = arrayOf(),

    @set:Argument("external-prefixes", alias = "ex-prefixes", delimiter = ":", description = "The classes from the external libraries. The Verifier will not report 'No such class' for such classes.")
    var externalClassesPrefixes: Array<String> = arrayOf()

)

fun File.create(): File {
  if (this.parentFile != null) {
    FileUtils.forceMkdir(this.parentFile)
  }
  this.createNewFile()
  return this
}

object CmdUtil {


  @Throws(IOException::class)
  fun createIde(ideToCheck: File, opts: CmdOpts): Ide {
    return IdeManager.getInstance().createIde(ideToCheck, takeVersionFromCmd(opts))
  }

  @Throws(IOException::class)
  fun takeVersionFromCmd(opts: CmdOpts): IdeVersion? {
    val build = opts.actualIdeVersion
    if (!build.isNullOrBlank()) {
      try {
        return IdeVersion.createIdeVersion(build!!)
      } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Incorrect update IDE-version has been specified " + build, e)
      }

    }
    return null
  }

  //TODO: add support of custom JDK ?
  @Throws(IOException::class)
  fun getJdkDir(opts: CmdOpts): File {
    val runtimeDirectory: File

    if (opts.runtimeDir != null) {
      runtimeDirectory = File(opts.runtimeDir)
      if (!runtimeDirectory.isDirectory) {
        throw RuntimeException("Specified runtime directory is not a directory: " + opts.runtimeDir)
      }
    } else {
      val javaHome = System.getenv("JAVA_HOME") ?: throw RuntimeException("JAVA_HOME is not specified")

      runtimeDirectory = File(javaHome)
      if (!runtimeDirectory.isDirectory) {
        throw RuntimeException("Invalid JAVA_HOME: " + javaHome)
      }
    }

    return runtimeDirectory
  }

  @Throws(IOException::class)
  fun getExternalClassPath(opts: CmdOpts): Resolver =
      Resolver.createUnionResolver("External classpath resolver: ${opts.externalClasspath}",
          opts.externalClasspath.map { Resolver.createJarResolver(File(it)) })


}

object VOptionsUtil {

  @JvmStatic
  fun parseOpts(opts: CmdOpts): VOptions {

    var problemsToIgnore: Multimap<Pair<String, String>, Pattern> = HashMultimap.create<Pair<String, String>, Pattern>()

    val ignoreProblemsFile = opts.ignoreProblemsFile
    if (ignoreProblemsFile != null) {
      problemsToIgnore = getProblemsToIgnoreFromFile(ignoreProblemsFile)
    }

    return VOptions(opts.externalClassesPrefixes.map { it.replace('.', '/') }.toTypedArray(), opts.ignoreMissingOptionalDependencies.toSet(), problemsToIgnore)
  }

  fun getProblemsToIgnoreFromFile(ignoreProblemsFile: String): Multimap<Pair<String, String>, Pattern> {
    val file = File(ignoreProblemsFile)
    if (!file.exists()) {
      throw IllegalArgumentException("Ignored problems file doesn't exist " + ignoreProblemsFile)
    }

    val m = HashMultimap.create<Pair<String, String>, Pattern>()
    try {
      BufferedReader(FileReader(file)).use { br ->
        var s: String?
        while (true) {
          s = br.readLine() ?: break
          s = s.trim { it <= ' ' }
          if (s.isEmpty() || s.startsWith("//")) continue //it is a comment

          val tokens = s.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

          if (tokens.size != 3) {
            throw IllegalArgumentException("incorrect problem line $s\nthe line must be in the form: <plugin_xml_id>:<plugin_version>:<problem_description_regexp_pattern>\n<plugin_version> may be empty (which means that a problem will be ignored in all the versions of the plugin)\nexample 'org.jetbrains.kotlin::accessing to unknown class org/jetbrains/kotlin/compiler/.*' - ignore all the missing classes from org.jetbrains.kotlin.compiler package")
          }

          val pluginId = tokens[0].trim { it <= ' ' }
          val pluginVersion = tokens[1].trim { it <= ' ' }
          val ignorePattern = tokens[2].trim { it <= ' ' }.replace('/', '.')

          m.put(pluginId to pluginVersion, Pattern.compile(ignorePattern))
        }
      }
    } catch (e: Exception) {
      throw RuntimeException("Unable to parse ignored problems file " + ignoreProblemsFile, e)
    }

    return m
  }

}
