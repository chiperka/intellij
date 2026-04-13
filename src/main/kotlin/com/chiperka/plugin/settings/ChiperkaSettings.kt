package com.chiperka.plugin.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(
    name = "com.chiperka.plugin.settings.ChiperkaSettings",
    storages = [Storage("Chiperka.xml")]
)
class ChiperkaSettings : PersistentStateComponent<ChiperkaSettings.State> {

    data class State(
        var executorType: String = EXECUTOR_LOCAL,
        var chiperkaPath: String = "chiperka",
        var dockerMode: String = DOCKER_EXEC,
        var dockerContainer: String = "",
        var dockerImage: String = "",
        var dockerChiperkaPath: String = "chiperka",
        var dockerPathMappingHost: String = "",
        var dockerPathMappingContainer: String = "",
        var composeFile: String = "",
        var composeService: String = "",
        var composeProjectName: String = "",
        var composeMode: String = DOCKER_EXEC,
        var composeChiperkaPath: String = "chiperka",
        var composePathMappingHost: String = "",
        var composePathMappingContainer: String = "",
        var configurationFile: String = ""
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    var executorType: String
        get() = state.executorType
        set(value) { state.executorType = value }

    var chiperkaPath: String
        get() = state.chiperkaPath
        set(value) { state.chiperkaPath = value }

    var dockerMode: String
        get() = state.dockerMode
        set(value) { state.dockerMode = value }

    var dockerContainer: String
        get() = state.dockerContainer
        set(value) { state.dockerContainer = value }

    var dockerImage: String
        get() = state.dockerImage
        set(value) { state.dockerImage = value }

    var dockerChiperkaPath: String
        get() = state.dockerChiperkaPath
        set(value) { state.dockerChiperkaPath = value }

    var composeFile: String
        get() = state.composeFile
        set(value) { state.composeFile = value }

    var composeService: String
        get() = state.composeService
        set(value) { state.composeService = value }

    var composeProjectName: String
        get() = state.composeProjectName
        set(value) { state.composeProjectName = value }

    var composeMode: String
        get() = state.composeMode
        set(value) { state.composeMode = value }

    var composeChiperkaPath: String
        get() = state.composeChiperkaPath
        set(value) { state.composeChiperkaPath = value }

    var dockerPathMappingHost: String
        get() = state.dockerPathMappingHost
        set(value) { state.dockerPathMappingHost = value }

    var dockerPathMappingContainer: String
        get() = state.dockerPathMappingContainer
        set(value) { state.dockerPathMappingContainer = value }

    var composePathMappingHost: String
        get() = state.composePathMappingHost
        set(value) { state.composePathMappingHost = value }

    var composePathMappingContainer: String
        get() = state.composePathMappingContainer
        set(value) { state.composePathMappingContainer = value }

    var configurationFile: String
        get() = state.configurationFile
        set(value) { state.configurationFile = value }

    fun mapPath(path: String): String {
        val (hostPrefix, containerPrefix) = when (executorType) {
            EXECUTOR_DOCKER -> dockerPathMappingHost to dockerPathMappingContainer
            EXECUTOR_DOCKER_COMPOSE -> composePathMappingHost to composePathMappingContainer
            else -> return path
        }
        if (hostPrefix.isBlank() || containerPrefix.isBlank()) return path
        if (!path.startsWith(hostPrefix)) return path
        return containerPrefix + path.removePrefix(hostPrefix)
    }

    fun pathMappingArg(): String {
        val (hostPrefix, containerPrefix) = when (executorType) {
            EXECUTOR_DOCKER -> dockerPathMappingHost to dockerPathMappingContainer
            EXECUTOR_DOCKER_COMPOSE -> composePathMappingHost to composePathMappingContainer
            else -> return ""
        }
        if (hostPrefix.isBlank() || containerPrefix.isBlank()) return ""
        return "$containerPrefix=$hostPrefix"
    }

    companion object {
        const val EXECUTOR_LOCAL = "local"
        const val EXECUTOR_DOCKER = "docker"
        const val EXECUTOR_DOCKER_COMPOSE = "docker-compose"
        const val DOCKER_EXEC = "exec"
        const val DOCKER_RUN = "run"

        fun getInstance(project: Project): ChiperkaSettings =
            project.getService(ChiperkaSettings::class.java)
    }
}
