package com.chiperkarunner.plugin.run

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
import com.chiperkarunner.plugin.settings.ChiperkaSettings

class ChiperkaCommandLineState(
    private val config: ChiperkaRunConfiguration,
    environment: ExecutionEnvironment
) : CommandLineState(environment) {

    override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
        val processHandler = startProcess()
        val properties = ChiperkaTestConsoleProperties(config, executor)
        val consoleView = SMTestRunnerConnectionUtil.createAndAttachConsole("Chiperka", processHandler, properties)
        return DefaultExecutionResult(consoleView, processHandler)
    }

    override fun startProcess(): ProcessHandler {
        val settings = ChiperkaSettings.getInstance(config.project)
        val commandLine = buildCommandLine(settings)
        val handler = ColoredProcessHandler(commandLine)
        return handler
    }

    private fun buildCommandLine(settings: ChiperkaSettings): GeneralCommandLine {
        val mappedTestPath = settings.mapPath(config.testFilePath)
        val chiperkaArgs = mutableListOf("run", mappedTestPath)

        // Always add --teamcity for IDE test runner integration
        chiperkaArgs.add("--teamcity")

        if (config.filterName.isNotBlank()) {
            chiperkaArgs.add("--filter")
            chiperkaArgs.add(config.filterName)
        }

        if (config.regenerateSnapshots) {
            chiperkaArgs.add("--regenerate-snapshots")
        }

        val effectiveConfigFile = config.configurationFile.ifBlank { settings.configurationFile }
        if (effectiveConfigFile.isNotBlank()) {
            chiperkaArgs.add("--configuration")
            chiperkaArgs.add(settings.mapPath(effectiveConfigFile))
        }

        val pathMappingArg = settings.pathMappingArg()
        if (pathMappingArg.isNotBlank()) {
            chiperkaArgs.add("--path-mapping")
            chiperkaArgs.add(pathMappingArg)
        }

        val effectiveCloudUrl = config.cloudUrl.ifBlank { settings.cloudUrl }
        if (config.cloudMode) {
            chiperkaArgs.add("--cloud")
        }

        val executorType = config.executorType.ifBlank { settings.executorType }
        val commandLine = GeneralCommandLine()

        when (executorType) {
            ChiperkaSettings.EXECUTOR_DOCKER -> {
                val chiperkaPath = settings.dockerChiperkaPath.ifBlank { "chiperka" }
                commandLine.exePath = "docker"
                if (settings.dockerMode == ChiperkaSettings.DOCKER_RUN) {
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
                commandLine.addParameter(chiperkaPath)
                commandLine.addParameters(chiperkaArgs)
            }
            ChiperkaSettings.EXECUTOR_DOCKER_COMPOSE -> {
                val composeFile = settings.composeFile
                val service = settings.composeService
                val chiperkaPath = settings.composeChiperkaPath.ifBlank { "chiperka" }
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
                if (settings.composeMode == ChiperkaSettings.DOCKER_RUN) {
                    commandLine.addParameter("--rm")
                }
                commandLine.addParameter(service)
                commandLine.addParameter(chiperkaPath)
                commandLine.addParameters(chiperkaArgs)
            }
            else -> {
                val chiperkaPath = settings.chiperkaPath.ifBlank { "chiperka" }
                commandLine.exePath = chiperkaPath
                commandLine.addParameters(chiperkaArgs)
            }
        }

        if (effectiveCloudUrl.isNotBlank()) {
            commandLine.withEnvironment("CHIPERKA_CLOUD_URL", effectiveCloudUrl)
        }

        commandLine.withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
        return commandLine
    }
}
