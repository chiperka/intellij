package com.sparkrunner.plugin.run

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.ui.FormBuilder
import javax.swing.JCheckBox
import javax.swing.JComponent

class SparkRunOptionsDialog(project: Project) : DialogWrapper(project) {

    private val cloudModeCheckBox = JCheckBox("Cloud mode")
    private val regenerateSnapshotsCheckBox = JCheckBox("Regenerate snapshots")

    init {
        title = "Run Spark Tests"
        setOKButtonText("Run")

        cloudModeCheckBox.isSelected = false
        cloudModeCheckBox.isEnabled = true

        regenerateSnapshotsCheckBox.isSelected = false

        init()
    }

    override fun createCenterPanel(): JComponent {
        return FormBuilder.createFormBuilder()
            .addComponent(cloudModeCheckBox)
            .addComponent(regenerateSnapshotsCheckBox)
            .panel
    }

    fun applyToConfig(config: SparkRunConfiguration) {
        config.cloudMode = cloudModeCheckBox.isSelected
        config.regenerateSnapshots = regenerateSnapshotsCheckBox.isSelected
    }
}
