package ru.endlesscode.bukkitgradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import ru.endlesscode.bukkitgradle.server.ServerConstants
import ru.endlesscode.bukkitgradle.server.ServerCore
import ru.endlesscode.bukkitgradle.server.extension.RunConfiguration
import ru.endlesscode.bukkitgradle.server.task.CreateIdeaJarRunConfiguration
import ru.endlesscode.bukkitgradle.server.task.GenerateRunningScript
import ru.endlesscode.bukkitgradle.server.task.PrepareServer
import ru.endlesscode.bukkitgradle.server.task.RunServer

class DevServerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        ServerCore serverCore = new ServerCore(project)
        def configuration = project.bukkit.run as RunConfiguration
        def generateRunningScript = project.tasks.register('generateRunningScript', GenerateRunningScript) {
            jvmArgs.set(configuration.buildJvmArgs())
            bukkitArgs.set(configuration.bukkitArgs)
            scriptDir.set(serverCore.serverDir)
        }

        def prepareServer = project.tasks.register('prepareServer', PrepareServer) {
            group = BukkitGradlePlugin.GROUP
            description = 'Prepare server ro run. Configure server and copy compiled plugin to plugins dir'
            core = serverCore
            dependsOn('build', 'copyServerCore')
        } as TaskProvider<PrepareServer>

        project.tasks.register('runServer', RunServer) {
            scriptFile.set(generateRunningScript.map { it.scriptFile.get().asFile })
            dependsOn(prepareServer)
        }

        project.tasks.register('buildIdeaRun', CreateIdeaJarRunConfiguration) {
            configurationName.set("$project.name: Run server")
            beforeRunTask.set('prepareServer')
            configurationsDir.set(project.rootProject.layout.projectDirectory.dir('.idea/runConfigurations'))
            jarPath.set(prepareServer.map { it.serverDir.file(ServerConstants.FILE_CORE).get().asFile })
        }
    }
}
