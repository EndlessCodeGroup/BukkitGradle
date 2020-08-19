package ru.endlesscode.bukkitgradle.server

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ServerProperties {

    private static final String NAME = "local.properties"

    private static Property buildToolsDir = new Property("buildtools.dir", "BUKKIT_BUILDTOOLS_HOME", "Path to BuildTools")
    private static Property devServerDir = new Property("server.dir", "BUKKIT_DEV_SERVER_HOME", "Path to Dev Server")

    private Logger logger = LoggerFactory.getLogger("RunConfiguration")
    private Properties properties = new Properties()

    private File propertiesFile

    ServerProperties(File projectPath) {
        propertiesFile = new File(projectPath, NAME)
        if (propertiesFile.exists()) {
            properties.load(propertiesFile.newReader("UTF-8"))
        } else {
            loadDefaults("$projectPath.absolutePath/build")
        }
    }

    private void loadDefaults(String defaultPath) {
        logger.info("$NAME file not found. Creating default...")

        setDefault(devServerDir, "$defaultPath/server")
        setDefault(buildToolsDir, "$defaultPath/buildtools")

        properties.store(propertiesFile.newWriter("UTF-8"), $/
 This file should *NOT* be checked into Version Control Systems,
 as it contains information specific to your local configuration./$)
    }

    private void setDefault(Property property, String defaultValue) {
        if (System.getenv(property.envVariable) == null) {
            properties.setProperty(property.name, defaultValue)
        }
    }

    File getBuildToolsDir() {
        return getDir(buildToolsDir)
    }

    File getDevServerDir() {
        return getDir(devServerDir)
    }

    private File getDir(Property property) {
        def value = get(property)
        if (value == null) return null

        def dir = new File(value).absoluteFile
        dir.mkdirs()

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
