package com.sparkrunner.plugin.run

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.testframework.sm.runner.SMTestLocator

class SparkTestConsoleProperties(
    config: RunConfiguration,
    executor: Executor
) : SMTRunnerConsoleProperties(config, "Spark", executor) {

    init {
        isIdBasedTestTree = true
    }

    override fun getTestLocator(): SMTestLocator = SparkTestLocator.INSTANCE
}
