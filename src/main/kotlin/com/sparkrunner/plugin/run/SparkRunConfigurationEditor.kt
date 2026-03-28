package com.sparkrunner.plugin.run

import com.intellij.openapi.options.SettingsEditor
import com.intellij.util.ui.FormBuilder
import com.sparkrunner.plugin.settings.SparkSettings
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class SparkRunConfigurationEditor : SettingsEditor<SparkRunConfiguration>() {

    private val executorTypeCombo = JComboBox(arrayOf(
        "Default (from Settings)",
        "Local",
        "Docker",
        "Docker Compose"
    ))
    private val testFilePathField = JTextField()
    private val filterNameField = JTextField()
    private val configurationFileField = JTextField()
    private val cloudUrlField = JTextField()
    private val cloudModeCheckBox = JCheckBox("Cloud mode")
    private val regenerateSnapshotsCheckBox = JCheckBox("Regenerate snapshots")

    override fun resetEditorFrom(config: SparkRunConfiguration) {
        testFilePathField.text = config.testFilePath
        filterNameField.text = config.filterName
        configurationFileField.text = config.configurationFile
        cloudUrlField.text = config.cloudUrl
        cloudModeCheckBox.isSelected = config.cloudMode
        regenerateSnapshotsCheckBox.isSelected = config.regenerateSnapshots
        executorTypeCombo.selectedIndex = when (config.executorType) {
            SparkSettings.EXECUTOR_LOCAL -> 1
            SparkSettings.EXECUTOR_DOCKER -> 2
            SparkSettings.EXECUTOR_DOCKER_COMPOSE -> 3
            else -> 0
        }
    }

    override fun applyEditorTo(config: SparkRunConfiguration) {
        config.testFilePath = testFilePathField.text
        config.filterName = filterNameField.text
        config.configurationFile = configurationFileField.text
        config.cloudUrl = cloudUrlField.text
        config.cloudMode = cloudModeCheckBox.isSelected
        config.regenerateSnapshots = regenerateSnapshotsCheckBox.isSelected
        config.executorType = when (executorTypeCombo.selectedIndex) {
            1 -> SparkSettings.EXECUTOR_LOCAL
            2 -> SparkSettings.EXECUTOR_DOCKER
            3 -> SparkSettings.EXECUTOR_DOCKER_COMPOSE
            else -> ""
        }
    }

    override fun createEditor(): JComponent {
        return FormBuilder.createFormBuilder()
            .addLabeledComponent("Interpreter:", executorTypeCombo)
            .addSeparator()
            .addLabeledComponent("Test file/folder:", testFilePathField)
            .addLabeledComponent("Filter:", filterNameField)
            .addLabeledComponent("Configuration:", configurationFileField)
            .addSeparator()
            .addLabeledComponent("Cloud URL (override):", cloudUrlField)
            .addComponent(cloudModeCheckBox)
            .addComponent(regenerateSnapshotsCheckBox)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}
