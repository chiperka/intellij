plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.2.1"
}

group = "com.chiperkarunner.plugin"
version = System.getenv("PLUGIN_VERSION") ?: error("PLUGIN_VERSION environment variable is required")

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2025.1")
        bundledPlugin("org.jetbrains.plugins.yaml")
        bundledPlugin("com.intellij.modules.json")
    }
}

kotlin {
    jvmToolchain(21)
}

intellijPlatform {
    pluginConfiguration {
        id = "com.sparkrunner.plugin"
        name = "Chiperka Test Runner"
        version = System.getenv("PLUGIN_VERSION") ?: error("PLUGIN_VERSION environment variable is required")
        vendor {
            name = "Finie"
            email = "info@finie.cz"
            url = "https://about.chiperka.com"
        }
        ideaVersion {
            sinceBuild = "251"
            untilBuild = provider { null }
        }
        changeNotes = providers.environmentVariable("PLUGIN_CHANGE_NOTES").orElse("")
    }
    publishing {
        token = providers.environmentVariable("JETBRAINS_MARKETPLACE_TOKEN")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.11.1"
    }
}
