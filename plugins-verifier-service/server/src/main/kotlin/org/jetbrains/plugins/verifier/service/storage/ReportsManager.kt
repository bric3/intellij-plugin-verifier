package org.jetbrains.plugins.verifier.service.storage

import com.intellij.structure.domain.IdeVersion
import com.jetbrains.pluginverifier.report.CheckIdeReport
import org.jetbrains.plugins.verifier.service.util.deleteLogged
import java.io.File

/**
 * @author Sergey Patrikeev
 */
object ReportsManager {
  @Synchronized
  fun saveReport(file: File): Boolean {
    val ideReport: CheckIdeReport
    try {
      ideReport = CheckIdeReport.loadFromFile(file)
    } catch(e: Exception) {
      throw IllegalArgumentException("Report file ${file.name} is invalid", e)
    }
    val report = FileManager.getFileByName(ideReport.ideVersion.asString(), FileType.REPORT)
    file.copyTo(report, true)
    return true
  }

  @Synchronized
  fun listReports(): List<IdeVersion> = FileManager.getFilesOfType(FileType.REPORT).map { IdeVersion.createIdeVersion(it.nameWithoutExtension) }

  @Synchronized
  fun deleteReport(ideVersion: IdeVersion): Boolean {
    val file = FileManager.getFileByName(ideVersion.asString(), FileType.REPORT)
    if (!file.exists()) {
      return false
    }
    return file.deleteLogged()
  }

  @Synchronized
  fun getReport(ideVersion: IdeVersion): File? {
    val file = FileManager.getFileByName(ideVersion.asString(), FileType.REPORT)
    if (file.exists()) {
      return file
    }
    return null
  }
}