package ru.endlesscode.bukkitgradle.server.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import ru.endlesscode.bukkitgradle.server.ServerCore
import ru.endlesscode.bukkitgradle.server.extension.RunConfiguration
import ru.endlesscode.bukkitgradle.server.script.RunningScript

class RunServer extends DefaultTask {
    @Input
    ServerCore core

    @TaskAction
    void runServer() {
        RunningScript script = this.createStartScript()
        logger.lifecycle("Running script built!")
        logger.lifecycle("Starting Server...")
        this.runScript(script)
        logger.lifecycle("Server started successfully!")
    }

    RunningScript createStartScript() {
        RunConfiguration configuration = project.bukkit.run
        RunningScript script = RunningScript.getScript(configuration, core.simpleVersion)
        script.buildOn(this.core.serverDir)

        return script
    }

    void runScript(RunningScript script) {
        new ProcessBuilder(script.command)
                .redirectErrorStream(true)
                .directory(core.serverDir)
                .start()
    }
}
