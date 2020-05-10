package ru.endlesscode.bukkitgradle.server

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ServerProperties {

    private static final String NAME = "local.properties"

    private static Property buildToolsDir = new Property("buildtools.dir", "BUKKIT_BUILDTOOLS_HOME", "Path to BuildTools")
    private static Property devServerDir = new Property("server.dir", "BUKKIT_DEV_SERVER_HOME", "Path to Dev Server")

    private Logger logger = LoggerFactory.getLogger("RunConfiguration")
    private Properties properties = new Properties()

    private Path propertiesFile

    ServerProperties(Path projectPath) {
        propertiesFile = projectPath.resolve(NAME)
        if (Files.exists(propertiesFile)) {
            properties.load(propertiesFile.newReader())
        } else {
            loadDefaults(projectPath.resolve("build").toAbsolutePath())
        }
    }

    private void loadDefaults(Path defaultPath) {
        logger.info("$NAME file not found. Creating default...")

        setDefault(devServerDir, defaultPath.resolve("server").toString())
        setDefault(buildToolsDir, defaultPath.resolve("buildtools").toString())

        properties.store(propertiesFile.newWriter(), $/
 This file should *NOT* be checked into Version Control Systems,
 as it contains information specific to your local configuration./$)
    }

    private void setDefault(Property property, String defaultValue) {
        if (System.getenv(property.envVariable) == null) {
            properties.setProperty(property.name, defaultValue)
        }
    }

    Path getBuildToolsDir() {
        return getDir(buildToolsDir)
    }

    Path getDevServerDir() {
        return getDir(devServerDir)
    }

    private Path getDir(Property property) {
        def value = get(property)
        if (value == null) return null

        def dir = Paths.get()
        Files.createDirectories(dir)

        return dir
    }

    private String get(Property property) {
        def localProp = properties.getProperty(property.name)
        def globalEnv = System.getenv(property.envVariable)
        if (localProp == null && globalEnv == null) {
            logger.warn($/
                $property.description not found. It can be fixed by two ways:
                   1. Define variable "$property.name" in the $NAME file
                   2. Define $property.envVariable environment variable
                /$.stripIndent()
            )
            return null
        }

        return localProp ?: globalEnv
    }

    private static class Property {

        String name
        String envVariable
        private String description

        Property(String name, String envVariable, String description) {
            this.name = name
            this.envVariable = envVariable
            this.description = description
        }
    }
}
