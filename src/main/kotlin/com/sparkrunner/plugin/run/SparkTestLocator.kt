package com.chiperkarunner.plugin.run

import com.intellij.execution.Location
import com.intellij.execution.PsiLocation
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.yaml.psi.YAMLFile
import org.jetbrains.yaml.psi.YAMLMapping
import org.jetbrains.yaml.psi.YAMLSequence

/**
 * SMTestLocator for Chiperka test files.
 *
 * Parses locationHint URLs in the format:
 *   chiperka:///path/to/file.chiperka::testName   (navigates to test)
 *   chiperka:///path/to/file.chiperka             (navigates to suite/file)
 *
 * When a user clicks a test in the Test Runner tree, IntelliJ calls this locator
 * to resolve the source location and navigate to the corresponding .chiperka file.
 */
class ChiperkaTestLocator : SMTestLocator {

    companion object {
        val INSTANCE = ChiperkaTestLocator()
        const val PROTOCOL = "chiperka"
    }

    override fun getLocation(
        protocol: String,
        path: String,
        project: Project,
        scope: GlobalSearchScope
    ): List<Location<*>> {
        if (protocol != PROTOCOL) return emptyList()

        val (filePath, testName) = parsePath(path)

        val virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath)
            ?: return emptyList()
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
            ?: return emptyList()

        // If no test name, navigate to the file (suite level)
        if (testName == null) {
            return listOf(PsiLocation.fromPsiElement(psiFile))
        }

        // Find the test element by name in the YAML structure
        val yamlFile = psiFile as? YAMLFile ?: return listOf(PsiLocation.fromPsiElement(psiFile))
        val testElement = findTestElement(yamlFile, testName)
        if (testElement != null) {
            return listOf(PsiLocation.fromPsiElement(testElement))
        }

        // Fallback to file
        return listOf(PsiLocation.fromPsiElement(psiFile))
    }

    /**
     * Parses "path/to/file.chiperka::testName" into (filePath, testName).
     * If no "::" separator, testName is null.
     */
    private fun parsePath(path: String): Pair<String, String?> {
        val idx = path.indexOf("::")
        if (idx == -1) return path to null
        return path.substring(0, idx) to path.substring(idx + 2)
    }

    /**
     * Finds the YAML key-value element for a test name within a .chiperka file.
     * Looks in the "tests" array for a mapping with name: <testName>.
     */
    private fun findTestElement(yamlFile: YAMLFile, testName: String): com.intellij.psi.PsiElement? {
        val doc = yamlFile.documents.firstOrNull() ?: return null
        val topMapping = doc.topLevelValue as? YAMLMapping ?: return null

        val testsKey = topMapping.getKeyValueByKey("tests") ?: return null
        val testsSequence = testsKey.value as? YAMLSequence ?: return null

        for (item in testsSequence.items) {
            val mapping = item.value as? YAMLMapping ?: continue
            val nameKV = mapping.getKeyValueByKey("name") ?: continue
            if (nameKV.valueText == testName) {
                return nameKV
            }
        }

        return null
    }
}
