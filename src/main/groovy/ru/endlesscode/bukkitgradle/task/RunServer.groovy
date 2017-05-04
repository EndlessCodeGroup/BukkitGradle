package ru.endlesscode.bukkitgradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import ru.endlesscode.bukkitgradle.extension.RunConfiguration
import ru.endlesscode.bukkitgradle.server.ServerCore
import ru.endlesscode.bukkitgradle.server.script.SystemScript

class RunServer extends DefaultTask {
    @Input
    ServerCore core

    @TaskAction
    void runServer() {
        SystemScript script = this.createStartScript()
        logger.lifecycle("Running script built!")
        logger.lifecycle("Starting Server...")
        this.runScript(script)
        logger.lifecycle("Server started successfully!")
    }

    SystemScript createStartScript() {
        RunConfiguration configuration = project.bukkit.run
        SystemScript script = SystemScript.getScript(configuration, core.simpleVersion)
        script.buildOn(this.core.serverDir)

        return script
    }

    void runScript(SystemScript script) {
        new ProcessBuilder(script.command)
                .redirectErrorStream(true)
                .directory(core.serverDir.toFile())
                .start()
    }
}
