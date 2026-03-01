package com.sparkrunner.plugin.action

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.sparkrunner.plugin.SparkIcons

class CreateSparkTestAction : CreateFileFromTemplateAction(
    "Spark Test",
    "Create a new Spark test file",
    SparkIcons.Spark,
) {
    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle("New Spark Test")
            .addKind("Spark Test", SparkIcons.Spark, "Spark Test")
    }

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String {
        return "Create Spark Test $newName"
    }
}
