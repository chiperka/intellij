package com.sparkrunner.plugin.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.sparkrunner.plugin.SparkIcons
import com.sparkrunner.plugin.run.SparkRunUtil

class SparkRunFolderAction : AnAction("Run Spark Tests", "Run Spark tests", SparkIcons.Spark) {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = file != null &&
            (file.isDirectory || file.name.endsWith(".spark"))
        e.presentation.text = if (file != null && !file.isDirectory) "Run Spark Test" else "Run Spark Tests"
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        val targetPath = file.path
        val configName = if (file.isDirectory) "Spark: ${file.name}/" else "Spark: ${file.name}"

        val settings = SparkRunUtil.findOrCreateConfig(project, targetPath, null, configName)
        SparkRunUtil.showDialogAndRun(project, settings)
    }
}
