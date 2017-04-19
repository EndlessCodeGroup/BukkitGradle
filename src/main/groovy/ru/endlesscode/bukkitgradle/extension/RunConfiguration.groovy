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

    final List<String> javaArgs
    final List<String> bukkitArgs

    RunConfiguration(Project project) {
        this.project = project

        this.eula = false
        this.onlineMode = false
        this.debug = true
        this.encoding = "UTF-8"
        this.dir = "server"

        this.javaArgs = ["-Xmx1G"]
        this.bukkitArgs = []
    }

    /**
     * Returns arguments for java
     *
     * @return Java arguments
     */
    List<String> getJavaArgs() {
        def javaArgs = []
        if (this.debug) {
            javaArgs << DEBUG_ARGS
        }
        javaArgs << "-Dfile.encoding=$encoding"
        javaArgs.addAll(this.javaArgs)

        return javaArgs
    }

    void setJavaArgs(String args) {
        javaArgs.clear()
        javaArgs.addAll(args.split(" "))
    }

    /**
     * Returns arguments for bukkit
     *
     * @return Bukkit arguments
     */
    List<String> getBukkitArgs() {
        def bukkitArgs = [] << "-o" << "$onlineMode"
        bukkitArgs.addAll(this.bukkitArgs)

        return bukkitArgs
    }

    void setBukkitArgs(String args) {
        bukkitArgs.clear()
        bukkitArgs.addAll(args.split(" "))
    }

    Path getDir() {
        project.projectDir.toPath().resolve(this.dir)
    }
}
