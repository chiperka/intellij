package com.chiperka.plugin

import com.intellij.openapi.fileTypes.LanguageFileType
import org.jetbrains.yaml.YAMLLanguage
import javax.swing.Icon

class ChiperkaFileType private constructor() : LanguageFileType(YAMLLanguage.INSTANCE) {
    companion object {
        @JvmField
        val INSTANCE = ChiperkaFileType()
    }

    override fun getName(): String = "Chiperka"
    override fun getDescription(): String = "Chiperka test file"
    override fun getDefaultExtension(): String = "chiperka"
    override fun getIcon(): Icon = ChiperkaIcons.Chiperka
}
