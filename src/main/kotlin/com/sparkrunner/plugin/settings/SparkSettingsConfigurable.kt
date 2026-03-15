package com.sparkrunner.plugin.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.ui.FormBuilder
import java.awt.CardLayout
import java.awt.Color
import java.awt.FlowLayout
import java.awt.event.ItemEvent
import javax.swing.*

class SparkSettingsConfigurable(private val project: Project) : Configurable {

    private var executorTypeCombo: JComboBox<String>? = null
    private var executorCards: JPanel? = null

    // Local
    private var sparkPathField: TextFieldWithBrowseButton? = null

    // Docker
    private var dockerModeCombo: JComboBox<String>? = null
    private var dockerContainerField: JTextField? = null
    private var dockerImageField: JTextField? = null
    private var dockerSparkPathField: JTextField? = null
    private var dockerModeCards: JPanel? = null
    private var dockerPathMappingHostField: JTextField? = null
    private var dockerPathMappingContainerField: JTextField? = null

    // Docker Compose
    private var composeFileField: TextFieldWithBrowseButton? = null
    private var composeServiceField: JTextField? = null
    private var composeProjectNameField: JTextField? = null
    private var composeModeCombo: JComboBox<String>? = null
    private var composeSparkPathField: JTextField? = null
    private var composePathMappingHostField: JTextField? = null
    private var composePathMappingContainerField: JTextField? = null

    // Common
    private var cloudUrlField: JTextField? = null
    private var configurationFileField: TextFieldWithBrowseButton? = null
    private var testResultLabel: JLabel? = null
    private var panel: JPanel? = null

    override fun getDisplayName(): String = "Spark Test Runner"

    override fun createComponent(): JComponent {
        executorTypeCombo = JComboBox(arrayOf("Local", "Docker", "Docker Compose"))

        // Local panel
        sparkPathField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                "Select Spark Executable",
                "Path to the spark binary",
                null,
                FileChooserDescriptorFactory.createSingleFileDescriptor()
            )
        }
        val localPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Spark executable:", sparkPathField!!)
            .panel

        // Docker panel
        dockerModeCombo = JComboBox(arrayOf("exec", "run"))
        dockerContainerField = JTextField()
        dockerImageField = JTextField()
        dockerSparkPathField = JTextField()

        val dockerModeCardLayout = CardLayout()
        dockerModeCards = JPanel(dockerModeCardLayout)
        val execPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Container name:", dockerContainerField!!)
            .panel
        val runPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Image:", dockerImageField!!)
            .panel
        dockerModeCards!!.add(execPanel, "exec")
        dockerModeCards!!.add(runPanel, "run")

        dockerModeCombo!!.addItemListener { e ->
            if (e.stateChange == ItemEvent.SELECTED) {
                dockerModeCardLayout.show(dockerModeCards, e.item as String)
            }
        }

        dockerPathMappingHostField = JTextField()
        dockerPathMappingContainerField = JTextField()

        val dockerPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Mode:", dockerModeCombo!!)
            .addComponent(dockerModeCards!!)
            .addLabeledComponent("Spark path in container:", dockerSparkPathField!!)
            .addSeparator()
            .addLabeledComponent("Path mapping - host path:", dockerPathMappingHostField!!)
            .addLabeledComponent("Path mapping - container path:", dockerPathMappingContainerField!!)
            .panel

        // Docker Compose panel
        composeFileField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                "Select Docker Compose File",
                "Path to docker-compose.yml",
                null,
                FileChooserDescriptorFactory.createSingleFileDescriptor()
            )
        }
        composeServiceField = JTextField()
        composeProjectNameField = JTextField()
        composeModeCombo = JComboBox(arrayOf("exec", "run"))
        composeSparkPathField = JTextField()
        composePathMappingHostField = JTextField()
        composePathMappingContainerField = JTextField()
        val composePanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Compose file:", composeFileField!!)
            .addLabeledComponent("Service name:", composeServiceField!!)
            .addLabeledComponent("Project name:", composeProjectNameField!!)
            .addLabeledComponent("Mode:", composeModeCombo!!)
            .addLabeledComponent("Spark path in container:", composeSparkPathField!!)
            .addSeparator()
            .addLabeledComponent("Path mapping - host path:", composePathMappingHostField!!)
            .addLabeledComponent("Path mapping - container path:", composePathMappingContainerField!!)
            .panel

        // Card layout for executor types
        val cardLayout = CardLayout()
        executorCards = JPanel(cardLayout)
        executorCards!!.add(localPanel, "Local")
        executorCards!!.add(dockerPanel, "Docker")
        executorCards!!.add(composePanel, "Docker Compose")

        executorTypeCombo!!.addItemListener { e ->
            if (e.stateChange == ItemEvent.SELECTED) {
                cardLayout.show(executorCards, e.item as String)
            }
        }

        // Common fields
        cloudUrlField = JTextField()
        configurationFileField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                "Select Configuration File",
                "Path to spark.yaml configuration file",
                null,
                FileChooserDescriptorFactory.createSingleFileDescriptor()
            )
        }

        // Test connection button
        val testButton = JButton("Test")
        testResultLabel = JLabel()
        testButton.addActionListener { runTestConnection() }
        val testPanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))
        testPanel.add(testButton)
        testPanel.add(Box.createHorizontalStrut(8))
        testPanel.add(testResultLabel)

        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Interpreter:", executorTypeCombo!!)
            .addComponent(executorCards!!)
            .addSeparator()
            .addLabeledComponent("Configuration file:", configurationFileField!!)
            .addLabeledComponent("Cloud URL:", cloudUrlField!!)
            .addSeparator()
            .addComponent(testPanel)
            .addComponentFillVertically(JPanel(), 0)
            .panel

        reset()
        return panel!!
    }

    private fun runTestConnection() {
        testResultLabel?.text = "Testing..."
        testResultLabel?.foreground = UIManager.getColor("Label.foreground")

        Thread {
            val command = buildTestCommand()
            try {
                val process = ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start()
                val output = process.inputStream.bufferedReader().readText().trim()
                val exitCode = process.waitFor()
                SwingUtilities.invokeLater {
                    if (exitCode == 0 && output.isNotBlank()) {
                        testResultLabel?.text = "\u2705 $output"
                        testResultLabel?.foreground = Color(0x59, 0xA8, 0x69)
                    } else {
                        val msg = output.ifBlank { "exit code $exitCode" }
                        testResultLabel?.text = "\u274C $msg"
                        testResultLabel?.foreground = Color(0xDB, 0x56, 0x56)
                    }
                }
            } catch (e: Exception) {
                SwingUtilities.invokeLater {
                    testResultLabel?.text = "\u274C ${e.message}"
                    testResultLabel?.foreground = Color(0xDB, 0x56, 0x56)
                }
            }
        }.start()
    }

    private fun buildTestCommand(): List<String> {
        return when (getSelectedExecutorType()) {
            SparkSettings.EXECUTOR_DOCKER -> {
                val sparkPath = dockerSparkPathField?.text?.ifBlank { "spark" } ?: "spark"
                val mode = if (dockerModeCombo?.selectedIndex == 1) SparkSettings.DOCKER_RUN else SparkSettings.DOCKER_EXEC
                if (mode == SparkSettings.DOCKER_RUN) {
                    val image = dockerImageField?.text ?: ""
                    listOf("docker", "run", "--rm", image, sparkPath, "--version")
                } else {
                    val container = dockerContainerField?.text ?: ""
                    listOf("docker", "exec", container, sparkPath, "--version")
                }
            }
            SparkSettings.EXECUTOR_DOCKER_COMPOSE -> {
                val sparkPath = composeSparkPathField?.text?.ifBlank { "spark" } ?: "spark"
                val composeFile = composeFileField?.text ?: ""
                val service = composeServiceField?.text ?: ""
                val projectName = composeProjectNameField?.text ?: ""
                val mode = if (composeModeCombo?.selectedIndex == 1) SparkSettings.DOCKER_RUN else SparkSettings.DOCKER_EXEC
                val args = mutableListOf("docker", "compose")
                if (composeFile.isNotBlank()) {
                    args.addAll(listOf("-f", composeFile))
                }
                if (projectName.isNotBlank()) {
                    args.addAll(listOf("-p", projectName))
                }
                args.add(mode)
                if (mode == SparkSettings.DOCKER_RUN) {
                    args.add("--rm")
                }
                args.add(service)
                args.add(sparkPath)
                args.add("--version")
                args
            }
            else -> {
                val sparkPath = sparkPathField?.text?.ifBlank { "spark" } ?: "spark"
                listOf(sparkPath, "--version")
            }
        }
    }

    override fun isModified(): Boolean {
        val s = SparkSettings.getInstance(project)
        return getSelectedExecutorType() != s.executorType
            || sparkPathField?.text != s.sparkPath
            || getSelectedDockerMode() != s.dockerMode
            || dockerContainerField?.text != s.dockerContainer
            || dockerImageField?.text != s.dockerImage
            || dockerSparkPathField?.text != s.dockerSparkPath
            || dockerPathMappingHostField?.text != s.dockerPathMappingHost
            || dockerPathMappingContainerField?.text != s.dockerPathMappingContainer
            || composeFileField?.text != s.composeFile
            || composeServiceField?.text != s.composeService
            || composeProjectNameField?.text != s.composeProjectName
            || getSelectedComposeMode() != s.composeMode
            || composeSparkPathField?.text != s.composeSparkPath
            || composePathMappingHostField?.text != s.composePathMappingHost
            || composePathMappingContainerField?.text != s.composePathMappingContainer
            || cloudUrlField?.text != s.cloudUrl
            || configurationFileField?.text != s.configurationFile
    }

    override fun apply() {
        val s = SparkSettings.getInstance(project)
        s.executorType = getSelectedExecutorType()
        s.sparkPath = sparkPathField?.text ?: "spark"
        s.dockerMode = getSelectedDockerMode()
        s.dockerContainer = dockerContainerField?.text ?: ""
        s.dockerImage = dockerImageField?.text ?: ""
        s.dockerSparkPath = dockerSparkPathField?.text ?: "spark"
        s.dockerPathMappingHost = dockerPathMappingHostField?.text ?: ""
        s.dockerPathMappingContainer = dockerPathMappingContainerField?.text ?: ""
        s.composeFile = composeFileField?.text ?: ""
        s.composeService = composeServiceField?.text ?: ""
        s.composeProjectName = composeProjectNameField?.text ?: ""
        s.composeMode = getSelectedComposeMode()
        s.composeSparkPath = composeSparkPathField?.text ?: "spark"
        s.composePathMappingHost = composePathMappingHostField?.text ?: ""
        s.composePathMappingContainer = composePathMappingContainerField?.text ?: ""
        s.cloudUrl = cloudUrlField?.text ?: ""
        s.configurationFile = configurationFileField?.text ?: ""
    }

    override fun reset() {
        val s = SparkSettings.getInstance(project)
        executorTypeCombo?.selectedIndex = when (s.executorType) {
            SparkSettings.EXECUTOR_DOCKER -> 1
            SparkSettings.EXECUTOR_DOCKER_COMPOSE -> 2
            else -> 0
        }
        sparkPathField?.text = s.sparkPath
        dockerModeCombo?.selectedIndex = if (s.dockerMode == SparkSettings.DOCKER_RUN) 1 else 0
        dockerContainerField?.text = s.dockerContainer
        dockerImageField?.text = s.dockerImage
        dockerSparkPathField?.text = s.dockerSparkPath
        val projectBasePath = project.basePath ?: ""
        dockerPathMappingHostField?.text = s.dockerPathMappingHost.ifBlank { projectBasePath }
        dockerPathMappingContainerField?.text = s.dockerPathMappingContainer.ifBlank { "/code" }
        composeFileField?.text = s.composeFile
        composeServiceField?.text = s.composeService
        composeProjectNameField?.text = s.composeProjectName
        composeModeCombo?.selectedIndex = if (s.composeMode == SparkSettings.DOCKER_RUN) 1 else 0
        composeSparkPathField?.text = s.composeSparkPath
        composePathMappingHostField?.text = s.composePathMappingHost.ifBlank { projectBasePath }
        composePathMappingContainerField?.text = s.composePathMappingContainer.ifBlank { "/code" }
        cloudUrlField?.text = s.cloudUrl
        configurationFileField?.text = s.configurationFile

        val cardLayout = executorCards?.layout as? CardLayout
        cardLayout?.show(executorCards, executorTypeCombo?.selectedItem as? String ?: "Local")

        val dockerCardLayout = dockerModeCards?.layout as? CardLayout
        dockerCardLayout?.show(dockerModeCards, s.dockerMode)

        testResultLabel?.text = ""
    }

    override fun disposeUIResources() {
        executorTypeCombo = null
        executorCards = null
        sparkPathField = null
        dockerModeCombo = null
        dockerContainerField = null
        dockerImageField = null
        dockerSparkPathField = null
        dockerModeCards = null
        dockerPathMappingHostField = null
        dockerPathMappingContainerField = null
        composeFileField = null
        composeServiceField = null
        composeProjectNameField = null
        composeModeCombo = null
        composeSparkPathField = null
        composePathMappingHostField = null
        composePathMappingContainerField = null
        cloudUrlField = null
        configurationFileField = null
        testResultLabel = null
        panel = null
    }

    private fun getSelectedExecutorType(): String = when (executorTypeCombo?.selectedIndex) {
        1 -> SparkSettings.EXECUTOR_DOCKER
        2 -> SparkSettings.EXECUTOR_DOCKER_COMPOSE
        else -> SparkSettings.EXECUTOR_LOCAL
    }

    private fun getSelectedDockerMode(): String =
        if (dockerModeCombo?.selectedIndex == 1) SparkSettings.DOCKER_RUN else SparkSettings.DOCKER_EXEC

    private fun getSelectedComposeMode(): String =
        if (composeModeCombo?.selectedIndex == 1) SparkSettings.DOCKER_RUN else SparkSettings.DOCKER_EXEC
}
