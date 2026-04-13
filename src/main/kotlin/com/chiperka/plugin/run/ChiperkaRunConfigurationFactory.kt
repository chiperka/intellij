package com.chiperka.plugin.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project

class ChiperkaRunConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {

    override fun getId(): String = ChiperkaRunConfigurationType.ID

    override fun createTemplateConfiguration(project: Project): RunConfiguration =
        ChiperkaRunConfiguration(project, this, "Chiperka Test")

    override fun getOptionsClass(): Class<out BaseState> =
        ChiperkaRunConfigurationOptions::class.java
}
