package com.sparkrunner.plugin.run

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.sparkrunner.plugin.SparkIcons

class SparkRunConfigurationType : ConfigurationTypeBase(
    ID,
    "Spark Test",
    "Run Spark test files",
    SparkIcons.Spark
) {
    init {
        addFactory(SparkRunConfigurationFactory(this))
    }

    companion object {
        const val ID = "SparkRunConfiguration"
    }
}
