package com.chiperkarunner.plugin.lineMarker

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.chiperkarunner.plugin.run.ChiperkaRunUtil
import org.jetbrains.yaml.psi.*

class ChiperkaTestLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        val file = element.containingFile ?: return null
        if (!file.name.endsWith(".chiperka")) return null

        val yamlFile = file as? YAMLFile ?: return null
        if (isServiceTemplate(yamlFile)) return null

        val keyValue = element.parent as? YAMLKeyValue ?: return null
        if (keyValue.keyText != "name") return null
        if (element !== keyValue.key) return null

        val suiteName = getSuiteName(yamlFile)

        return when {
            isSuiteLevelName(keyValue) -> createSuiteMarker(element, file.virtualFile.path, suiteName)
            isTestLevelName(keyValue) -> {
                val testName = keyValue.valueText
                val displayName = if (suiteName.isNotBlank()) "$suiteName > $testName" else testName
                createTestMarker(element, file.virtualFile.path, displayName, testName)
            }
            else -> null
        }
    }

    private fun getSuiteName(yamlFile: YAMLFile): String {
        val doc = yamlFile.documents.firstOrNull() ?: return ""
        val topMapping = doc.topLevelValue as? YAMLMapping ?: return ""
        return topMapping.getKeyValueByKey("name")?.valueText ?: ""
    }

    private fun isServiceTemplate(yamlFile: YAMLFile): Boolean {
        val doc = yamlFile.documents.firstOrNull() ?: return false
        val topMapping = doc.topLevelValue as? YAMLMapping ?: return false
        val typeValue = topMapping.getKeyValueByKey("type")?.valueText
        return typeValue == "service"
    }

    private fun isSuiteLevelName(keyValue: YAMLKeyValue): Boolean {
        val parentMapping = keyValue.parent as? YAMLMapping ?: return false
        return parentMapping.parent is YAMLDocument
    }

    private fun isTestLevelName(keyValue: YAMLKeyValue): Boolean {
        val parentMapping = keyValue.parent as? YAMLMapping ?: return false
        val sequenceItem = parentMapping.parent as? YAMLSequenceItem ?: return false
        val sequence = sequenceItem.parent as? YAMLSequence ?: return false
        val testsKeyValue = sequence.parent as? YAMLKeyValue ?: return false
        return testsKeyValue.keyText == "tests"
    }

    private fun createSuiteMarker(element: PsiElement, filePath: String, suiteName: String): LineMarkerInfo<PsiElement> {
        val label = if (suiteName.isNotBlank()) "Run '$suiteName'" else "Run suite"
        return LineMarkerInfo(
            element,
            element.textRange,
            AllIcons.RunConfigurations.TestState.Run,
            { label },
            { _, psiElement -> runChiperka(psiElement, filePath, null, suiteName.ifBlank { null }) },
            GutterIconRenderer.Alignment.CENTER,
            { label }
        )
    }

    private fun createTestMarker(element: PsiElement, filePath: String, displayName: String, filterName: String): LineMarkerInfo<PsiElement> {
        return LineMarkerInfo(
            element,
            element.textRange,
            AllIcons.RunConfigurations.TestState.Run,
            { "Run '$displayName'" },
            { _, psiElement -> runChiperka(psiElement, filePath, filterName, displayName) },
            GutterIconRenderer.Alignment.CENTER,
            { "Run '$displayName'" }
        )
    }

    private fun runChiperka(element: PsiElement, filePath: String, filterName: String?, displayName: String?) {
        val project = element.project

        val configName = if (displayName != null && displayName.isNotBlank()) {
            "Chiperka: $displayName"
        } else {
            "Chiperka: ${filePath.substringAfterLast('/')}"
        }

        val settings = ChiperkaRunUtil.findOrCreateConfig(project, filePath, filterName, configName)
        ChiperkaRunUtil.showDialogAndRun(project, settings)
    }
}
