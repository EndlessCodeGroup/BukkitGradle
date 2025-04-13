package ru.endlesscode.bukkitgradle.server

import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

internal class DeprecatedServerProperties(rootDir: File, private val providers: ProviderFactory) {

    val devServerDir: File?

    private val logger = LoggerFactory.getLogger("DeprecatedServerProperties")
    private val properties = Properties()

    private val propertiesFile = File(rootDir, NAME)

    init {
        if (propertiesFile.exists()) {
            properties.load(propertiesFile.reader())
        }

        // Read properties eagerly to show deprecation warning
        devServerDir = getDir(DEV_SERVER_DIR)
        getDir(BUILD_TOOLS_DIR)
    }

    private fun getDir(property: Property): File? {
        val path = get(property) ?: return null

        showDeprecationWarning(property)
        return File(path)
            .absoluteFile
            .also { it.mkdirs() }
    }

    private fun get(property: Property): String? {
        val localProp = properties.getProperty(property.name)
        val globalEnv = getEnvProvider(property.envVariable)
        return localProp ?: globalEnv.orNull
    }

    private fun getEnvProvider(name: String): Provider<String> {
        return providers.environmentVariable(name)
    }

    private fun showDeprecationWarning(property: Property) {
        logger.warn(
            """
            Property '${property.name}' is Deprecated.
            ${property.replacementNote}
            Please, remove the property from $NAME and unset '${property.envVariable}' environment variable.
            """.trimIndent()
        )
    }

    private data class Property(
        val name: String,
        val envVariable: String,
        val replacementNote: String,
    )

    private companion object {
        private const val NAME: String = "local.properties"

        private val BUILD_TOOLS_DIR: Property = Property(
            name = "buildtools.dir",
            envVariable = "BUKKIT_BUILDTOOLS_HOME",
            replacementNote = "Building and running Spigot server is not supported anymore.",
        )
        private val DEV_SERVER_DIR: Property = Property(
            name = "server.dir",
            envVariable = "BUKKIT_DEV_SERVER_HOME",
            replacementNote = "Set Gradle property 'bukkitgradle.server.dir' in \$HOME/.gradle/gradle.properties instead.",
        )
    }
}
