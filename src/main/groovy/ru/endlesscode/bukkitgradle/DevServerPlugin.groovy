package ru.endlesscode.bukkitgradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import ru.endlesscode.bukkitgradle.server.ServerConstants
import ru.endlesscode.bukkitgradle.server.ServerCore
import ru.endlesscode.bukkitgradle.server.task.CreateIdeaJarRunConfiguration
import ru.endlesscode.bukkitgradle.server.task.PrepareServer
import ru.endlesscode.bukkitgradle.server.task.RunServer

class DevServerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        ServerCore serverCore = new ServerCore(project)
        project.tasks.register('runServer', RunServer) {
            group = BukkitGradlePlugin.GROUP
            description = 'Run dev server'
            core = serverCore
            dependsOn('prepareServer')
        }

        def prepareServer = project.tasks.register('prepareServer', PrepareServer) {
            group = BukkitGradlePlugin.GROUP
            description = 'Prepare server ro run. Configure server and copy compiled plugin to plugins dir'
            core = serverCore
            dependsOn('build', 'copyServerCore')
        } as TaskProvider<PrepareServer>

        project.tasks.register('buildIdeaRun', CreateIdeaJarRunConfiguration) {
            configurationName.set("$project.name: Run server")
            beforeRunTask.set('prepareServer')
            configurationsDir.set(project.rootProject.layout.projectDirectory.dir('.idea/runConfigurations'))
            jarPath.set(prepareServer.get().serverDir
                    .map { it.file(ServerConstants.FILE_CORE).asFile.path })
        }
    }
}
