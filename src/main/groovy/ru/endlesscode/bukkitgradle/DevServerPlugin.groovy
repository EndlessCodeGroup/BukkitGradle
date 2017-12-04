package ru.endlesscode.bukkitgradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.impldep.org.apache.maven.lifecycle.LifecycleExecutionException
import ru.endlesscode.bukkitgradle.server.ServerCore
import ru.endlesscode.bukkitgradle.task.PrepareServer
import ru.endlesscode.bukkitgradle.task.RunServer

import java.nio.file.Files
import java.nio.file.Path

class DevServerPlugin implements Plugin<Project> {
    Project project

    @Override
    void apply(Project project) {
        this.project = project
        ServerCore serverCore = new ServerCore(project)
        project.task('runServer', type: RunServer, dependsOn: 'prepareServer') {
            group = BukkitGradlePlugin.GROUP
            description = 'Run dev server'
            core serverCore
        }

        PrepareServer prepareServer = project.task(
                'prepareServer',
                type: PrepareServer,
                dependsOn: ['build', 'buildServerCore', 'copyServerCore']
        ) {
            group = BukkitGradlePlugin.GROUP
            description = 'Prepare server ro run. Configure server and copy compiled plugin to plugins dir'
            core serverCore
        } as PrepareServer

        Path runConfigurationsDir = project.projectDir.toPath().resolve(".idea/runConfigurations")
        project.task('buildIdeaRun', dependsOn: 'prepareServer') {
            group = BukkitGradlePlugin.GROUP
            description = 'Configure IDEA server run configuration'
        }.doLast {
            if (Files.notExists(runConfigurationsDir.parent)) {
                throw new LifecycleExecutionException("This task only for IntelliJ IDEA.")
            }

            Files.createDirectories(runConfigurationsDir)
            prepareServer.run.buildIdeaConfiguration(runConfigurationsDir)
        }

        project.afterEvaluate {
            prepareServer.run.buildIdeaConfiguration(runConfigurationsDir)
        }
    }
}
