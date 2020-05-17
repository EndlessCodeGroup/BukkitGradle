package ru.endlesscode.bukkitgradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import ru.endlesscode.bukkitgradle.server.ServerCore
import ru.endlesscode.bukkitgradle.server.idea.IdeaRunConfigurationBuilder
import ru.endlesscode.bukkitgradle.server.task.PrepareServer
import ru.endlesscode.bukkitgradle.server.task.RunServer

import java.nio.file.Files
import java.nio.file.Path

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

        Path runConfigurationsDir = project.rootDir.toPath().resolve(".idea/runConfigurations")
        project.tasks.register('buildIdeaRun') {
            group = BukkitGradlePlugin.GROUP
            description = 'Configure IDEA server run configuration'

            onlyIf { Files.exists(runConfigurationsDir.parent) }

            dependsOn(prepareServer)

            doLast {
                Files.createDirectories(runConfigurationsDir)
                def serverDir = prepareServer.get().serverDir.toRealPath()
                IdeaRunConfigurationBuilder.build(runConfigurationsDir, serverDir, prepareServer.get().run)
            }
        }

        project.afterEvaluate {
            def serverDir = prepareServer.get().serverDir.toRealPath()
            IdeaRunConfigurationBuilder.build(runConfigurationsDir, serverDir, prepareServer.get().run)
        }
    }
}
