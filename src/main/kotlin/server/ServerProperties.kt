package ru.endlesscode.bukkitgradle.server

import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

public class ServerProperties(projectPath: File) {

    private val logger = LoggerFactory.getLogger("RunConfiguration")
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

        setDefault(devServerDir, "$defaultPath/server")
        setDefault(buildToolsDir, "$defaultPath/buildtools")

        properties.store(
            propertiesFile.writer(),
            """
            This file should * NOT * be checked into Version Control Systems,
            as it contains information specific to your local configuration.""".trimIndent()
        )
    }

    private fun setDefault(property: Property, defaultValue: String) {
        if (System.getenv(property.envVariable) == null) {
            properties.setProperty(property.name, defaultValue)
        }
    }

    public fun getBuildToolsDir(): File? = getDir(buildToolsDir)

    public fun getDevServerDir(): File? = getDir(devServerDir)

    private fun getDir(property: Property): File? {
        val value = get(property) ?: return null

        val dir = File(value).absoluteFile
        dir.mkdirs()

        return dir
    }

    private fun get(property: Property): String? {
        val localProp = properties.getProperty(property.name)
        val globalEnv = System.getenv(property.envVariable)
        if (localProp == null && globalEnv == null) {
            logger.warn(
                """
                ${property.description} not found. It can be fixed by two ways:
                1.Define variable "${property.name}" in the $NAME file
                2.Define ${property.envVariable} environment variable
                """.trimIndent()
            )
            return null
        }

        return localProp ?: globalEnv
    }

    private data class Property(val name: String, val envVariable: String, val description: String)

    private companion object {
        private const val NAME: String = "local.properties"

        private val buildToolsDir: Property = Property(
            name = "buildtools.dir",
            envVariable = "BUKKIT_BUILDTOOLS_HOME",
            description = "Path to BuildTools"
        )
        private val devServerDir: Property = Property(
            name = "server.dir",
            envVariable = "BUKKIT_DEV_SERVER_HOME",
            description = "Path to Dev Server"
        )
    }
}
