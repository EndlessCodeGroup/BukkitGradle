package ru.endlesscode.bukkitgradle.server

import org.gradle.api.InvalidUserDataException
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

internal class ServerProperties(projectPath: File, private val providers: ProviderFactory) {

    val devServerDir: File
        get() = getDir(DEV_SERVER_DIR)

    val buildToolsDir: File
        get() = getDir(BUILD_TOOLS_DIR)

    private val logger = LoggerFactory.getLogger("ServerProperties")
    private val properties = Properties()

    private val propertiesFile = File(projectPath, NAME)

    init {
        if (propertiesFile.exists()) {
            properties.load(propertiesFile.reader())
        } else {
            loadDefaults("${projectPath.absolutePath}/build")
        }
    }

    private fun loadDefaults(defaultPath: String) {
        logger.info("$NAME file not found. Creating default...")

        setDefault(DEV_SERVER_DIR, "$defaultPath/server")
        setDefault(BUILD_TOOLS_DIR, "$defaultPath/buildtools")

        properties.store(
            propertiesFile.writer(),
            """
            This file should *NOT* be checked into Version Control Systems,
            as it contains information specific to your local configuration.
            """.trimIndent().trimEnd()
        )
    }

    private fun setDefault(property: Property, defaultValue: String) {
        if (!getEnvProvider(property.envVariable).isPresent) {
            properties.setProperty(property.name, defaultValue)
        }
    }

    private fun getDir(property: Property): File {
        return File(get(property))
            .absoluteFile
            .also { it.mkdirs() }
    }

    private fun get(property: Property): String {
        val localProp = properties.getProperty(property.name)
        val globalEnv = getEnvProvider(property.envVariable).orNull
        return localProp ?: globalEnv ?: showError(property)
    }

    private fun getEnvProvider(name: String): Provider<String> {
        return providers.environmentVariable(name).forUseAtConfigurationTime()
    }

    private fun showError(property: Property): Nothing {
        logger.error(
            """
                ${property.description} not found. It can be fixed by two ways:
                1.Define variable "${property.name}" in the $NAME file
                2.Define ${property.envVariable} environment variable
                """.trimIndent()
        )
        throw InvalidUserDataException()
    }

    private data class Property(val name: String, val envVariable: String, val description: String)

    private companion object {
        private const val NAME: String = "local.properties"

        private val BUILD_TOOLS_DIR: Property = Property(
            name = "buildtools.dir",
            envVariable = "BUKKIT_BUILDTOOLS_HOME",
            description = "Path to BuildTools"
        )
        private val DEV_SERVER_DIR: Property = Property(
            name = "server.dir",
            envVariable = "BUKKIT_DEV_SERVER_HOME",
            description = "Path to Dev Server"
        )
    }
}
