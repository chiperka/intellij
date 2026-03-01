package com.sparkrunner.plugin

import com.intellij.openapi.fileTypes.LanguageFileType
import org.jetbrains.yaml.YAMLLanguage
import javax.swing.Icon

class SparkFileType private constructor() : LanguageFileType(YAMLLanguage.INSTANCE) {
    companion object {
        @JvmField
        val INSTANCE = SparkFileType()
    }

    override fun getName(): String = "Spark"
    override fun getDescription(): String = "Spark test file"
    override fun getDefaultExtension(): String = "spark"
    override fun getIcon(): Icon = SparkIcons.Spark
}
