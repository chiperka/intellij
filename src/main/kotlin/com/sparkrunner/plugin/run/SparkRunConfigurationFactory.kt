package com.sparkrunner.plugin.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project

class SparkRunConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {

    override fun getId(): String = SparkRunConfigurationType.ID

    override fun createTemplateConfiguration(project: Project): RunConfiguration =
        SparkRunConfiguration(project, this, "Spark Test")

    override fun getOptionsClass(): Class<out BaseState> =
        SparkRunConfigurationOptions::class.java
}
