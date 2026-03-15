package com.sparkrunner.plugin.run

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
// ProcessTerminatedListener intentionally NOT used - it conflicts with SMRunner's
// own process lifecycle handling and causes the test tree to disappear after completion.
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.sparkrunner.plugin.settings.SparkSettings

class SparkCommandLineState(
    private val config: SparkRunConfiguration,
    environment: ExecutionEnvironment
) : CommandLineState(environment) {

    override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
        val processHandler = startProcess()
        val properties = SparkTestConsoleProperties(config, executor)
        val consoleView = SMTestRunnerConnectionUtil.createAndAttachConsole("Spark", processHandler, properties)
        return DefaultExecutionResult(consoleView, processHandler)
    }

    override fun startProcess(): ProcessHandler {
        val settings = SparkSettings.getInstance(config.project)
        val commandLine = buildCommandLine(settings)
        val handler = ColoredProcessHandler(commandLine)
        return handler
    }

    private fun buildCommandLine(settings: SparkSettings): GeneralCommandLine {
        val mappedTestPath = settings.mapPath(config.testFilePath)
        val sparkArgs = mutableListOf("run", mappedTestPath)

        // Always add --teamcity for IDE test runner integration
        sparkArgs.add("--teamcity")

        if (config.filterName.isNotBlank()) {
            sparkArgs.add("--filter")
            sparkArgs.add(config.filterName)
        }

        if (config.regenerateSnapshots) {
            sparkArgs.add("--regenerate-snapshots")
        }

        val effectiveConfigFile = config.configurationFile.ifBlank { settings.configurationFile }
        if (effectiveConfigFile.isNotBlank()) {
            sparkArgs.add("--configuration")
            sparkArgs.add(settings.mapPath(effectiveConfigFile))
        }

        val pathMappingArg = settings.pathMappingArg()
        if (pathMappingArg.isNotBlank()) {
            sparkArgs.add("--path-mapping")
            sparkArgs.add(pathMappingArg)
        }

        val effectiveCloudUrl = config.cloudUrl.ifBlank { settings.cloudUrl }
        if (config.cloudMode) {
            sparkArgs.add("--cloud")
        }

        val executorType = config.executorType.ifBlank { settings.executorType }
        val commandLine = GeneralCommandLine()

        when (executorType) {
            SparkSettings.EXECUTOR_DOCKER -> {
                val sparkPath = settings.dockerSparkPath.ifBlank { "spark" }
                commandLine.exePath = "docker"
                if (settings.dockerMode == SparkSettings.DOCKER_RUN) {
                    val image = settings.dockerImage
                    commandLine.addParameter("run")
                    commandLine.addParameter("--rm")
                    commandLine.addParameter("-i")
                    commandLine.addParameter(image)
                } else {
                    val container = settings.dockerContainer
                    commandLine.addParameter("exec")
                    commandLine.addParameter("-i")
                    commandLine.addParameter(container)
                }
                commandLine.addParameter(sparkPath)
                commandLine.addParameters(sparkArgs)
            }
            SparkSettings.EXECUTOR_DOCKER_COMPOSE -> {
                val composeFile = settings.composeFile
                val service = settings.composeService
                val sparkPath = settings.composeSparkPath.ifBlank { "spark" }
                commandLine.exePath = "docker"
                commandLine.addParameter("compose")
                if (composeFile.isNotBlank()) {
                    commandLine.addParameter("-f")
                    commandLine.addParameter(composeFile)
                }
                val projectName = settings.composeProjectName
                if (projectName.isNotBlank()) {
                    commandLine.addParameter("-p")
                    commandLine.addParameter(projectName)
                }
                commandLine.addParameter(settings.composeMode)
                commandLine.addParameter("-i")
                if (settings.composeMode == SparkSettings.DOCKER_RUN) {
                    commandLine.addParameter("--rm")
                }
                commandLine.addParameter(service)
                commandLine.addParameter(sparkPath)
                commandLine.addParameters(sparkArgs)
            }
            else -> {
                val sparkPath = settings.sparkPath.ifBlank { "spark" }
                commandLine.exePath = sparkPath
                commandLine.addParameters(sparkArgs)
            }
        }

        if (effectiveCloudUrl.isNotBlank()) {
            commandLine.withEnvironment("SPARK_CLOUD_URL", effectiveCloudUrl)
        }

        commandLine.withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
        return commandLine
    }
}
