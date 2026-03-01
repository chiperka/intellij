package com.sparkrunner.plugin.run

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.project.Project

object SparkRunUtil {

    fun findOrCreateConfig(
        project: Project,
        filePath: String,
        filterName: String?,
        configName: String
    ): RunnerAndConfigurationSettings {
        val runManager = RunManager.getInstance(project)
        val effectiveFilter = filterName ?: ""

        val existing = runManager.allSettings.find { setting ->
            val config = setting.configuration as? SparkRunConfiguration ?: return@find false
            config.testFilePath == filePath && config.filterName == effectiveFilter
        }

        if (existing != null) return existing

        val configType = SparkRunConfigurationType()
        val factory = configType.configurationFactories.first()
        val newSettings = runManager.createConfiguration(configName, factory)
        val config = newSettings.configuration as SparkRunConfiguration
        config.testFilePath = filePath
        config.filterName = effectiveFilter
        runManager.addConfiguration(newSettings)
        return newSettings
    }

    fun showDialogAndRun(project: Project, settings: RunnerAndConfigurationSettings) {
        val config = settings.configuration as? SparkRunConfiguration ?: return
        val dialog = SparkRunOptionsDialog(project)
        if (!dialog.showAndGet()) return

        dialog.applyToConfig(config)
        val runManager = RunManager.getInstance(project)
        runManager.selectedConfiguration = settings
        ProgramRunnerUtil.executeConfiguration(settings, DefaultRunExecutor.getRunExecutorInstance())
    }
}
