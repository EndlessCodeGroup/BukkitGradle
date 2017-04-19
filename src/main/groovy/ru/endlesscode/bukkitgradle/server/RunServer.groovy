package ru.endlesscode.bukkitgradle.server

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class RunServer extends DefaultTask {
    @Input
    ServerCore core

    @TaskAction
    void runServer() {
        final ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "start", "java", "-Xmx1G", "-jar", "core.jar")
        processBuilder.redirectErrorStream(true)
        processBuilder.directory(core.serverDir.toFile())

        logger.lifecycle("Starting Server...")
        processBuilder.start()
        logger.lifecycle("Server started successfully!")
    }
}
