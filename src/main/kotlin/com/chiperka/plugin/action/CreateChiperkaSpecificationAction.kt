package com.chiperka.plugin.action

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.chiperka.plugin.ChiperkaIcons

class CreateChiperkaSpecificationAction : CreateFileFromTemplateAction(
    "Chiperka Specification",
    "Create a new Chiperka specification file",
    ChiperkaIcons.Chiperka,
) {
    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle("New Chiperka Specification")
            .addKind("Test", ChiperkaIcons.Chiperka, "Chiperka Test")
            .addKind("Service", ChiperkaIcons.Chiperka, "Chiperka Service")
    }

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String {
        return "Create Chiperka Specification $newName"
    }
}
