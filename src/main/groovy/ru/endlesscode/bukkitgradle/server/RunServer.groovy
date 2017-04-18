package ru.endlesscode.bukkitgradle.server

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class RunServer extends DefaultTask {
    @Input
    ServerCore core

    @TaskAction
    void launchServer() {
        final ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "start", "java", "-Xmx1G", "-jar", "core.jar")
        processBuilder.redirectErrorStream(true)
        processBuilder.directory(new File(project.buildDir, core.getShortVersion()))

        logger.lifecycle("Starting Server...")
        processBuilder.start()
        logger.lifecycle("Server started successfully!")
    }
}
