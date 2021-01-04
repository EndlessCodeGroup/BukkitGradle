package ru.endlesscode.bukkitgradle.server.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import ru.endlesscode.bukkitgradle.server.ServerCore
import ru.endlesscode.bukkitgradle.server.script.RunningScriptStrategy

class RunServer extends DefaultTask {
    @Input
    ServerCore core

    @TaskAction
    void runServer() {
        RunningScriptStrategy script = this.createStartScript()
        logger.lifecycle("Running script built!")
        logger.lifecycle("Starting Server...")
        this.runScript(script)
        logger.lifecycle("Server started successfully!")
    }

    RunningScriptStrategy createStartScript() {
        RunningScriptStrategy script = RunningScriptStrategy.get()
        script.buildOn(this.core.serverDir)

        return script
    }

    void runScript(RunningScriptStrategy script) {
        new ProcessBuilder(script.command)
                .redirectErrorStream(true)
                .directory(core.serverDir)
                .start()
    }
}
