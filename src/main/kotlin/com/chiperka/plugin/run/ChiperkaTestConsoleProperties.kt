package com.chiperka.plugin.run

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.testframework.sm.runner.SMTestLocator

class ChiperkaTestConsoleProperties(
    config: RunConfiguration,
    executor: Executor
) : SMTRunnerConsoleProperties(config, "Chiperka", executor) {

    init {
        isIdBasedTestTree = true
    }

    override fun getTestLocator(): SMTestLocator = ChiperkaTestLocator.INSTANCE
}
