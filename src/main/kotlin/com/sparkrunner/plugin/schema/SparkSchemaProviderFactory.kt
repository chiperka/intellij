package com.sparkrunner.plugin.schema

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory
import com.jetbrains.jsonSchema.extension.SchemaType

class SparkSchemaProviderFactory : JsonSchemaProviderFactory {
    override fun getProviders(project: Project): List<JsonSchemaFileProvider> {
        return listOf(
            SparkTestSchemaProvider(project),
            SparkConfigSchemaProvider(project),
        )
    }
}

private class SparkTestSchemaProvider(private val project: Project) : JsonSchemaFileProvider {
    override fun isAvailable(file: VirtualFile): Boolean {
        return file.extension == "spark"
    }

    override fun getName(): String = "Spark Test"

    override fun getSchemaFile(): VirtualFile? {
        return JsonSchemaProviderFactory.getResourceFile(
            SparkSchemaProviderFactory::class.java,
            "/schemas/spark-test.schema.json"
        )
    }

    override fun getSchemaType(): SchemaType = SchemaType.embeddedSchema
}

private class SparkConfigSchemaProvider(private val project: Project) : JsonSchemaFileProvider {
    override fun isAvailable(file: VirtualFile): Boolean {
        val name = file.name
        return name == "spark.yaml" || name == "spark.yml"
    }

    override fun getName(): String = "Spark Config"

    override fun getSchemaFile(): VirtualFile? {
        return JsonSchemaProviderFactory.getResourceFile(
            SparkSchemaProviderFactory::class.java,
            "/schemas/spark-config.schema.json"
        )
    }

    override fun getSchemaType(): SchemaType = SchemaType.embeddedSchema
}
