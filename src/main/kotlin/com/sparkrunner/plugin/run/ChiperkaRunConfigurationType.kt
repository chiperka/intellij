package com.chiperkarunner.plugin.run

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.chiperkarunner.plugin.ChiperkaIcons

class ChiperkaRunConfigurationType : ConfigurationTypeBase(
    ID,
    "Chiperka Test",
    "Run Chiperka test files",
    ChiperkaIcons.Chiperka
) {
    init {
        addFactory(ChiperkaRunConfigurationFactory(this))
    }

    companion object {
        const val ID = "ChiperkaRunConfiguration"
    }
}
