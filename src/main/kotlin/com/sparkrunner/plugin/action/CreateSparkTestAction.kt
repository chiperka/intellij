package com.chiperkarunner.plugin.action

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.chiperkarunner.plugin.ChiperkaIcons

class CreateChiperkaTestAction : CreateFileFromTemplateAction(
    "Chiperka Test",
    "Create a new Chiperka test file",
    ChiperkaIcons.Chiperka,
) {
    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle("New Chiperka Test")
            .addKind("Chiperka Test", ChiperkaIcons.Chiperka, "Chiperka Test")
    }

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String {
        return "Create Chiperka Test $newName"
    }
}
