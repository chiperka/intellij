package com.sparkrunner.plugin.run

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project

class SparkRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : RunConfigurationBase<SparkRunConfigurationOptions>(project, factory, name) {

    override fun getOptions(): SparkRunConfigurationOptions =
        super.getOptions() as SparkRunConfigurationOptions

    var testFilePath: String
        get() = options.testFilePath
        set(value) { options.testFilePath = value }

    var filterName: String
        get() = options.filterName
        set(value) { options.filterName = value }

    var executorType: String
        get() = options.executorType
        set(value) { options.executorType = value }

    var cloudUrl: String
        get() = options.cloudUrl
        set(value) { options.cloudUrl = value }

    var additionalArgs: String
        get() = options.additionalArgs
        set(value) { options.additionalArgs = value }

    var configurationFile: String
        get() = options.configurationFile
        set(value) { options.configurationFile = value }

    var regenerateSnapshots: Boolean
        get() = options.regenerateSnapshots
        set(value) { options.regenerateSnapshots = value }

    var cloudMode: Boolean
        get() = options.cloudMode
        set(value) { options.cloudMode = value }

    override fun getConfigurationEditor(): SettingsEditor<SparkRunConfiguration> =
        SparkRunConfigurationEditor()

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState =
        SparkCommandLineState(this, environment)
}
