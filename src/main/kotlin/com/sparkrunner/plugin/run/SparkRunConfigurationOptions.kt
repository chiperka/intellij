package com.sparkrunner.plugin.run

import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.openapi.components.StoredProperty

class SparkRunConfigurationOptions : RunConfigurationOptions() {

    private val testFilePathProperty: StoredProperty<String?> =
        string("").provideDelegate(this, "testFilePath")

    private val filterNameProperty: StoredProperty<String?> =
        string("").provideDelegate(this, "filterName")

    private val executorTypeProperty: StoredProperty<String?> =
        string("").provideDelegate(this, "executorType")

    private val cloudUrlProperty: StoredProperty<String?> =
        string("").provideDelegate(this, "cloudUrl")

    private val additionalArgsProperty: StoredProperty<String?> =
        string("").provideDelegate(this, "additionalArgs")

    private val configurationFileProperty: StoredProperty<String?> =
        string("").provideDelegate(this, "configurationFile")

    private val regenerateSnapshotsProperty: StoredProperty<Boolean> =
        property(false).provideDelegate(this, "regenerateSnapshots")

    private val cloudModeProperty: StoredProperty<Boolean> =
        property(false).provideDelegate(this, "cloudMode")

    var testFilePath: String
        get() = testFilePathProperty.getValue(this) ?: ""
        set(value) { testFilePathProperty.setValue(this, value) }

    var filterName: String
        get() = filterNameProperty.getValue(this) ?: ""
        set(value) { filterNameProperty.setValue(this, value) }

    var executorType: String
        get() = executorTypeProperty.getValue(this) ?: ""
        set(value) { executorTypeProperty.setValue(this, value) }

    var cloudUrl: String
        get() = cloudUrlProperty.getValue(this) ?: ""
        set(value) { cloudUrlProperty.setValue(this, value) }

    var additionalArgs: String
        get() = additionalArgsProperty.getValue(this) ?: ""
        set(value) { additionalArgsProperty.setValue(this, value) }

    var configurationFile: String
        get() = configurationFileProperty.getValue(this) ?: ""
        set(value) { configurationFileProperty.setValue(this, value) }

    var regenerateSnapshots: Boolean
        get() = regenerateSnapshotsProperty.getValue(this)
        set(value) { regenerateSnapshotsProperty.setValue(this, value) }

    var cloudMode: Boolean
        get() = cloudModeProperty.getValue(this)
        set(value) { cloudModeProperty.setValue(this, value) }
}
