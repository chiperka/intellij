package com.sparkrunner.plugin.structure

import com.intellij.ide.structureView.*
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import org.jetbrains.yaml.psi.*
import javax.swing.Icon
import com.intellij.icons.AllIcons

class SparkStructureViewFactory : PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder? {
        if (!psiFile.name.endsWith(".spark")) return null
        val yamlFile = psiFile as? YAMLFile ?: return null

        return object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?): StructureViewModel {
                return SparkStructureViewModel(yamlFile, editor)
            }
        }
    }
}

private class SparkStructureViewModel(
    file: YAMLFile,
    editor: Editor?,
) : StructureViewModelBase(file, editor, SparkFileElement(file)), StructureViewModel.ElementInfoProvider {

    override fun getSorters(): Array<Sorter> = arrayOf(Sorter.ALPHA_SORTER)

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean = false

    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean = false
}

private class SparkFileElement(private val file: YAMLFile) : PsiTreeElementBase<YAMLFile>(file) {

    override fun getPresentableText(): String {
        val doc = file.documents.firstOrNull() ?: return file.name
        val mapping = doc.topLevelValue as? YAMLMapping ?: return file.name
        val name = mapping.getKeyValueByKey("name")?.valueText
        return name ?: file.name
    }

    override fun getIcon(open: Boolean): Icon = AllIcons.Nodes.TestGroup

    override fun getChildrenBase(): Collection<StructureViewTreeElement> {
        val doc = file.documents.firstOrNull() ?: return emptyList()
        val mapping = doc.topLevelValue as? YAMLMapping ?: return emptyList()
        val testsKv = mapping.getKeyValueByKey("tests") ?: return emptyList()
        val seq = testsKv.value as? YAMLSequence ?: return emptyList()

        return seq.items.mapNotNull { item ->
            val testMapping = item.value as? YAMLMapping ?: return@mapNotNull null
            SparkTestElement(testMapping)
        }
    }
}

private class SparkTestElement(private val mapping: YAMLMapping) : PsiTreeElementBase<YAMLMapping>(mapping) {

    override fun getPresentableText(): String {
        return mapping.getKeyValueByKey("name")?.valueText ?: "unnamed test"
    }

    override fun getIcon(open: Boolean): Icon = AllIcons.Nodes.Test

    override fun getChildrenBase(): Collection<StructureViewTreeElement> {
        val children = mutableListOf<StructureViewTreeElement>()

        mapping.getKeyValueByKey("services")?.let { kv ->
            children.add(SparkSectionElement(kv, "services", AllIcons.Nodes.Deploy))
        }
        mapping.getKeyValueByKey("setup")?.let { kv ->
            children.add(SparkSectionElement(kv, "setup", AllIcons.Actions.Install))
        }
        mapping.getKeyValueByKey("execution")?.let { kv ->
            children.add(SparkSectionElement(kv, "execution", AllIcons.Actions.Execute))
        }
        mapping.getKeyValueByKey("assertions")?.let { kv ->
            children.add(SparkSectionElement(kv, "assertions", AllIcons.Nodes.EntryPoints))
        }
        mapping.getKeyValueByKey("teardown")?.let { kv ->
            children.add(SparkSectionElement(kv, "teardown", AllIcons.Actions.Uninstall))
        }

        return children
    }
}

private class SparkSectionElement(
    private val kv: YAMLKeyValue,
    private val label: String,
    private val sectionIcon: Icon,
) : PsiTreeElementBase<YAMLKeyValue>(kv) {

    override fun getPresentableText(): String = label

    override fun getIcon(open: Boolean): Icon = sectionIcon

    override fun getChildrenBase(): Collection<StructureViewTreeElement> {
        val value = kv.value ?: return emptyList()

        return when (label) {
            "services" -> servicesChildren(value)
            "setup", "teardown" -> setupChildren(value)
            "assertions" -> assertionChildren(value)
            "execution" -> executionChildren(value)
            else -> emptyList()
        }
    }

    private fun servicesChildren(value: YAMLValue): List<StructureViewTreeElement> {
        val seq = value as? YAMLSequence ?: return emptyList()
        return seq.items.mapNotNull { item ->
            val m = item.value as? YAMLMapping ?: return@mapNotNull null
            val name = m.getKeyValueByKey("name")?.valueText
                ?: m.getKeyValueByKey("ref")?.valueText
                ?: "unknown"
            val image = m.getKeyValueByKey("image")?.valueText
            val text = if (image != null) "$name ($image)" else name
            SparkLeafElement(m, text, AllIcons.Nodes.Plugin)
        }
    }

    private fun setupChildren(value: YAMLValue): List<StructureViewTreeElement> {
        val seq = value as? YAMLSequence ?: return emptyList()
        return seq.items.mapIndexedNotNull { i, item ->
            val m = item.value as? YAMLMapping ?: return@mapIndexedNotNull null
            val httpKv = m.getKeyValueByKey("http")
            val cliKv = m.getKeyValueByKey("cli")
            val text = when {
                httpKv != null -> {
                    val httpMapping = httpKv.value as? YAMLMapping
                    val req = httpMapping?.getKeyValueByKey("request")?.value as? YAMLMapping
                    val method = req?.getKeyValueByKey("method")?.valueText ?: ""
                    val url = req?.getKeyValueByKey("url")?.valueText ?: ""
                    "http: $method $url"
                }
                cliKv != null -> {
                    val cliMapping = cliKv.value as? YAMLMapping
                    val cmd = cliMapping?.getKeyValueByKey("command")?.valueText ?: ""
                    "cli: $cmd"
                }
                else -> "step ${i + 1}"
            }
            SparkLeafElement(m, text, AllIcons.Nodes.RunnableMark)
        }
    }

    private fun assertionChildren(value: YAMLValue): List<StructureViewTreeElement> {
        val seq = value as? YAMLSequence ?: return emptyList()
        return seq.items.mapNotNull { item ->
            val m = item.value as? YAMLMapping ?: return@mapNotNull null
            val keys = m.keyValues.map { it.keyText }
            val type = keys.firstOrNull() ?: "unknown"
            val detail = when (type) {
                "statusCode", "exitCode" -> {
                    val inner = m.getKeyValueByKey(type)?.value as? YAMLMapping
                    val eq = inner?.getKeyValueByKey("equals")?.valueText
                    if (eq != null) "$type = $eq" else type
                }
                "snapshot" -> {
                    val inner = m.getKeyValueByKey("snapshot")?.value as? YAMLMapping
                    val artifact = inner?.getKeyValueByKey("artifact")?.valueText ?: ""
                    "snapshot: $artifact"
                }
                "stdout", "stderr" -> {
                    val inner = m.getKeyValueByKey(type)?.value as? YAMLMapping
                    val op = inner?.keyValues?.firstOrNull()
                    if (op != null) "$type.${op.keyText}" else type
                }
                else -> type
            }
            SparkLeafElement(m, detail, AllIcons.Nodes.EntryPoints)
        }
    }

    private fun executionChildren(value: YAMLValue): List<StructureViewTreeElement> {
        val m = value as? YAMLMapping ?: return emptyList()
        val executor = m.getKeyValueByKey("executor")?.valueText ?: "http"
        val text = when (executor) {
            "http" -> {
                val req = m.getKeyValueByKey("request")?.value as? YAMLMapping
                val method = req?.getKeyValueByKey("method")?.valueText ?: ""
                val url = req?.getKeyValueByKey("url")?.valueText ?: ""
                val target = m.getKeyValueByKey("target")?.valueText ?: ""
                "$method $target$url"
            }
            "cli" -> {
                val cli = m.getKeyValueByKey("cli")?.value as? YAMLMapping
                val cmd = cli?.getKeyValueByKey("command")?.valueText ?: ""
                val svc = cli?.getKeyValueByKey("service")?.valueText ?: ""
                "$svc: $cmd"
            }
            else -> executor
        }
        return listOf(SparkLeafElement(m, text, AllIcons.Nodes.RunnableMark))
    }
}

private class SparkLeafElement(
    private val psi: YAMLMapping,
    private val text: String,
    private val leafIcon: Icon,
) : PsiTreeElementBase<YAMLMapping>(psi) {

    override fun getPresentableText(): String = text

    override fun getIcon(open: Boolean): Icon = leafIcon

    override fun getChildrenBase(): Collection<StructureViewTreeElement> = emptyList()
}
