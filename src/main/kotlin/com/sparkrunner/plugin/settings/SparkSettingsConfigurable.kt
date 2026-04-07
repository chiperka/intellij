package com.chiperkarunner.plugin.settings

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

class ChiperkaSettingsConfigurable(private val project: Project) : Configurable {

    private var executorTypeCombo: JComboBox<String>? = null
    private var executorCards: JPanel? = null

    // Local
    private var chiperkaPathField: TextFieldWithBrowseButton? = null

    // Docker
    private var dockerModeCombo: JComboBox<String>? = null
    private var dockerContainerField: JTextField? = null
    private var dockerImageField: JTextField? = null
    private var dockerChiperkaPathField: JTextField? = null
    private var dockerModeCards: JPanel? = null
    private var dockerPathMappingHostField: JTextField? = null
    private var dockerPathMappingContainerField: JTextField? = null

    // Docker Compose
    private var composeFileField: TextFieldWithBrowseButton? = null
    private var composeServiceField: JTextField? = null
    private var composeProjectNameField: JTextField? = null
    private var composeModeCombo: JComboBox<String>? = null
    private var composeChiperkaPathField: JTextField? = null
    private var composePathMappingHostField: JTextField? = null
    private var composePathMappingContainerField: JTextField? = null

    // Common
    private var cloudUrlField: JTextField? = null
    private var configurationFileField: TextFieldWithBrowseButton? = null
    private var testResultLabel: JLabel? = null
    private var panel: JPanel? = null

    override fun getDisplayName(): String = "Chiperka Test Runner"

    override fun createComponent(): JComponent {
        executorTypeCombo = JComboBox(arrayOf("Local", "Docker", "Docker Compose"))

        // Local panel
        chiperkaPathField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                "Select Chiperka Executable",
                "Path to the chiperka binary",
                null,
                FileChooserDescriptorFactory.createSingleFileDescriptor()
            )
        }
        val localPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Chiperka executable:", chiperkaPathField!!)
            .panel

        // Docker panel
        dockerModeCombo = JComboBox(arrayOf("exec", "run"))
        dockerContainerField = JTextField()
        dockerImageField = JTextField()
        dockerChiperkaPathField = JTextField()

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
            .addLabeledComponent("Chiperka path in container:", dockerChiperkaPathField!!)
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
        composeChiperkaPathField = JTextField()
        composePathMappingHostField = JTextField()
        composePathMappingContainerField = JTextField()
        val composePanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Compose file:", composeFileField!!)
            .addLabeledComponent("Service name:", composeServiceField!!)
            .addLabeledComponent("Project name:", composeProjectNameField!!)
            .addLabeledComponent("Mode:", composeModeCombo!!)
            .addLabeledComponent("Chiperka path in container:", composeChiperkaPathField!!)
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
                "Path to chiperka.yaml configuration file",
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
            .addLabeledComponent("Cloud URL (override):", cloudUrlField!!)
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
            ChiperkaSettings.EXECUTOR_DOCKER -> {
                val chiperkaPath = dockerChiperkaPathField?.text?.ifBlank { "chiperka" } ?: "chiperka"
                val mode = if (dockerModeCombo?.selectedIndex == 1) ChiperkaSettings.DOCKER_RUN else ChiperkaSettings.DOCKER_EXEC
                if (mode == ChiperkaSettings.DOCKER_RUN) {
                    val image = dockerImageField?.text ?: ""
                    listOf("docker", "run", "--rm", image, chiperkaPath, "--version")
                } else {
                    val container = dockerContainerField?.text ?: ""
                    listOf("docker", "exec", container, chiperkaPath, "--version")
                }
            }
            ChiperkaSettings.EXECUTOR_DOCKER_COMPOSE -> {
                val chiperkaPath = composeChiperkaPathField?.text?.ifBlank { "chiperka" } ?: "chiperka"
                val composeFile = composeFileField?.text ?: ""
                val service = composeServiceField?.text ?: ""
                val projectName = composeProjectNameField?.text ?: ""
                val mode = if (composeModeCombo?.selectedIndex == 1) ChiperkaSettings.DOCKER_RUN else ChiperkaSettings.DOCKER_EXEC
                val args = mutableListOf("docker", "compose")
                if (composeFile.isNotBlank()) {
                    args.addAll(listOf("-f", composeFile))
                }
                if (projectName.isNotBlank()) {
                    args.addAll(listOf("-p", projectName))
                }
                args.add(mode)
                if (mode == ChiperkaSettings.DOCKER_RUN) {
                    args.add("--rm")
                }
                args.add(service)
                args.add(chiperkaPath)
                args.add("--version")
                args
            }
            else -> {
                val chiperkaPath = chiperkaPathField?.text?.ifBlank { "chiperka" } ?: "chiperka"
                listOf(chiperkaPath, "--version")
            }
        }
    }

    override fun isModified(): Boolean {
        val s = ChiperkaSettings.getInstance(project)
        return getSelectedExecutorType() != s.executorType
            || chiperkaPathField?.text != s.chiperkaPath
            || getSelectedDockerMode() != s.dockerMode
            || dockerContainerField?.text != s.dockerContainer
            || dockerImageField?.text != s.dockerImage
            || dockerChiperkaPathField?.text != s.dockerChiperkaPath
            || dockerPathMappingHostField?.text != s.dockerPathMappingHost
            || dockerPathMappingContainerField?.text != s.dockerPathMappingContainer
            || composeFileField?.text != s.composeFile
            || composeServiceField?.text != s.composeService
            || composeProjectNameField?.text != s.composeProjectName
            || getSelectedComposeMode() != s.composeMode
            || composeChiperkaPathField?.text != s.composeChiperkaPath
            || composePathMappingHostField?.text != s.composePathMappingHost
            || composePathMappingContainerField?.text != s.composePathMappingContainer
            || cloudUrlField?.text != s.cloudUrl
            || configurationFileField?.text != s.configurationFile
    }

    override fun apply() {
        val s = ChiperkaSettings.getInstance(project)
        s.executorType = getSelectedExecutorType()
        s.chiperkaPath = chiperkaPathField?.text ?: "chiperka"
        s.dockerMode = getSelectedDockerMode()
        s.dockerContainer = dockerContainerField?.text ?: ""
        s.dockerImage = dockerImageField?.text ?: ""
        s.dockerChiperkaPath = dockerChiperkaPathField?.text ?: "chiperka"
        s.dockerPathMappingHost = dockerPathMappingHostField?.text ?: ""
        s.dockerPathMappingContainer = dockerPathMappingContainerField?.text ?: ""
        s.composeFile = composeFileField?.text ?: ""
        s.composeService = composeServiceField?.text ?: ""
        s.composeProjectName = composeProjectNameField?.text ?: ""
        s.composeMode = getSelectedComposeMode()
        s.composeChiperkaPath = composeChiperkaPathField?.text ?: "chiperka"
        s.composePathMappingHost = composePathMappingHostField?.text ?: ""
        s.composePathMappingContainer = composePathMappingContainerField?.text ?: ""
        s.cloudUrl = cloudUrlField?.text ?: ""
        s.configurationFile = configurationFileField?.text ?: ""
    }

    override fun reset() {
        val s = ChiperkaSettings.getInstance(project)
        executorTypeCombo?.selectedIndex = when (s.executorType) {
            ChiperkaSettings.EXECUTOR_DOCKER -> 1
            ChiperkaSettings.EXECUTOR_DOCKER_COMPOSE -> 2
            else -> 0
        }
        chiperkaPathField?.text = s.chiperkaPath
        dockerModeCombo?.selectedIndex = if (s.dockerMode == ChiperkaSettings.DOCKER_RUN) 1 else 0
        dockerContainerField?.text = s.dockerContainer
        dockerImageField?.text = s.dockerImage
        dockerChiperkaPathField?.text = s.dockerChiperkaPath
        val projectBasePath = project.basePath ?: ""
        dockerPathMappingHostField?.text = s.dockerPathMappingHost.ifBlank { projectBasePath }
        dockerPathMappingContainerField?.text = s.dockerPathMappingContainer.ifBlank { "/code" }
        composeFileField?.text = s.composeFile
        composeServiceField?.text = s.composeService
        composeProjectNameField?.text = s.composeProjectName
        composeModeCombo?.selectedIndex = if (s.composeMode == ChiperkaSettings.DOCKER_RUN) 1 else 0
        composeChiperkaPathField?.text = s.composeChiperkaPath
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
        chiperkaPathField = null
        dockerModeCombo = null
        dockerContainerField = null
        dockerImageField = null
        dockerChiperkaPathField = null
        dockerModeCards = null
        dockerPathMappingHostField = null
        dockerPathMappingContainerField = null
        composeFileField = null
        composeServiceField = null
        composeProjectNameField = null
        composeModeCombo = null
        composeChiperkaPathField = null
        composePathMappingHostField = null
        composePathMappingContainerField = null
        cloudUrlField = null
        configurationFileField = null
        testResultLabel = null
        panel = null
    }

    private fun getSelectedExecutorType(): String = when (executorTypeCombo?.selectedIndex) {
        1 -> ChiperkaSettings.EXECUTOR_DOCKER
        2 -> ChiperkaSettings.EXECUTOR_DOCKER_COMPOSE
        else -> ChiperkaSettings.EXECUTOR_LOCAL
    }

    private fun getSelectedDockerMode(): String =
        if (dockerModeCombo?.selectedIndex == 1) ChiperkaSettings.DOCKER_RUN else ChiperkaSettings.DOCKER_EXEC

    private fun getSelectedComposeMode(): String =
        if (composeModeCombo?.selectedIndex == 1) ChiperkaSettings.DOCKER_RUN else ChiperkaSettings.DOCKER_EXEC
}
