package com.chiperka.plugin.run

import com.intellij.openapi.options.SettingsEditor
import com.intellij.util.ui.FormBuilder
import com.chiperka.plugin.settings.ChiperkaSettings
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class ChiperkaRunConfigurationEditor : SettingsEditor<ChiperkaRunConfiguration>() {

    private val executorTypeCombo = JComboBox(arrayOf(
        "Default (from Settings)",
        "Local",
        "Docker",
        "Docker Compose"
    ))
    private val testFilePathField = JTextField()
    private val filterNameField = JTextField()
    private val configurationFileField = JTextField()
    private val cloudModeCheckBox = JCheckBox("Cloud mode")
    private val regenerateSnapshotsCheckBox = JCheckBox("Regenerate snapshots")

    override fun resetEditorFrom(config: ChiperkaRunConfiguration) {
        testFilePathField.text = config.testFilePath
        filterNameField.text = config.filterName
        configurationFileField.text = config.configurationFile
        cloudModeCheckBox.isSelected = config.cloudMode
        regenerateSnapshotsCheckBox.isSelected = config.regenerateSnapshots
        executorTypeCombo.selectedIndex = when (config.executorType) {
            ChiperkaSettings.EXECUTOR_LOCAL -> 1
            ChiperkaSettings.EXECUTOR_DOCKER -> 2
            ChiperkaSettings.EXECUTOR_DOCKER_COMPOSE -> 3
            else -> 0
        }
    }

    override fun applyEditorTo(config: ChiperkaRunConfiguration) {
        config.testFilePath = testFilePathField.text
        config.filterName = filterNameField.text
        config.configurationFile = configurationFileField.text
        config.cloudMode = cloudModeCheckBox.isSelected
        config.regenerateSnapshots = regenerateSnapshotsCheckBox.isSelected
        config.executorType = when (executorTypeCombo.selectedIndex) {
            1 -> ChiperkaSettings.EXECUTOR_LOCAL
            2 -> ChiperkaSettings.EXECUTOR_DOCKER
            3 -> ChiperkaSettings.EXECUTOR_DOCKER_COMPOSE
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
            .addComponent(cloudModeCheckBox)
            .addComponent(regenerateSnapshotsCheckBox)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}
