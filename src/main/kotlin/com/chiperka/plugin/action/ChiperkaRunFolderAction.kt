package com.chiperka.plugin.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.chiperka.plugin.ChiperkaIcons
import com.chiperka.plugin.run.ChiperkaRunUtil

class ChiperkaRunFolderAction : AnAction("Run Chiperka Tests", "Run Chiperka tests", ChiperkaIcons.Chiperka) {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = file != null &&
            (file.isDirectory || file.name.endsWith(".chiperka"))
        e.presentation.text = if (file != null && !file.isDirectory) "Run Chiperka Test" else "Run Chiperka Tests"
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        val targetPath = file.path
        val configName = if (file.isDirectory) "Chiperka: ${file.name}/" else "Chiperka: ${file.name}"

        val settings = ChiperkaRunUtil.findOrCreateConfig(project, targetPath, null, configName)
        ChiperkaRunUtil.showDialogAndRun(project, settings)
    }
}
