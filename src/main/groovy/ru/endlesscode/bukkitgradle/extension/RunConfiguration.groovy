package ru.endlesscode.bukkitgradle.extension

import org.gradle.api.Project

import java.nio.file.Path

class RunConfiguration {
    private static final String DEBUG_ARGS = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

    private Project project

    boolean eula
    boolean onlineMode
    boolean debug
    String encoding
    String dir
    String javaArgs
    String bukkitArgs

    RunConfiguration(Project project) {
        this.project = project

        this.eula = false
        this.onlineMode = false
        this.debug = true
        this.encoding = "UTF-8"
        this.dir = "server"

        this.javaArgs = "-Xmx1G"
        this.bukkitArgs = ""
    }

    /**
     * Returns arguments for java
     *
     * @return Java arguments
     */
    String getJavaArgs() {
        return "${this.debug ? "$DEBUG_ARGS " : ""}-Dfile.encoding=$encoding ${this.javaArgs}"
    }

    /**
     * Returns arguments for bukkit
     *
     * @return Bukkit arguments
     */
    String getBukkitArgs() {
        return bukkitArgs ? " $bukkitArgs" : ""
    }

    /**
     * Returns servers dir
     *
     * @return The directory
     */
    Path getDir() {
        project.projectDir.toPath().resolve(this.dir)
    }
}
