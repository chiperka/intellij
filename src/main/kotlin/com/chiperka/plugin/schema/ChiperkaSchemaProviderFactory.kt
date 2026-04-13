package com.chiperka.plugin.schema

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory
import com.jetbrains.jsonSchema.extension.SchemaType

class ChiperkaSchemaProviderFactory : JsonSchemaProviderFactory {
    override fun getProviders(project: Project): List<JsonSchemaFileProvider> {
        return listOf(
            ChiperkaSpecSchemaProvider(project),
            ChiperkaConfigSchemaProvider(project),
        )
    }
}

private class ChiperkaSpecSchemaProvider(private val project: Project) : JsonSchemaFileProvider {
    override fun isAvailable(file: VirtualFile): Boolean {
        return file.extension == "chiperka"
    }

    override fun getName(): String = "Chiperka Specification"

    override fun getSchemaFile(): VirtualFile? {
        return JsonSchemaProviderFactory.getResourceFile(
            ChiperkaSchemaProviderFactory::class.java,
            "/schemas/chiperka-spec.schema.json"
        )
    }

    override fun getSchemaType(): SchemaType = SchemaType.embeddedSchema
}

private class ChiperkaConfigSchemaProvider(private val project: Project) : JsonSchemaFileProvider {
    override fun isAvailable(file: VirtualFile): Boolean {
        val name = file.name
        return name == "chiperka.yaml" || name == "chiperka.yml"
    }

    override fun getName(): String = "Chiperka Config"

    override fun getSchemaFile(): VirtualFile? {
        return JsonSchemaProviderFactory.getResourceFile(
            ChiperkaSchemaProviderFactory::class.java,
            "/schemas/chiperka-config.schema.json"
        )
    }

    override fun getSchemaType(): SchemaType = SchemaType.embeddedSchema
}
