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
        project.task("runServer", type: RunServer, dependsOn: "prepareServer") {
            core serverCore
        }.configure {
            group = BukkitGradlePlugin.GROUP
            description = 'Run dev server'
        }

        PrepareServer prepareServer = project.task("prepareServer", type: PrepareServer, dependsOn: ["build", "copyServerCore"]) {
            core serverCore
        }.configure {
            group = BukkitGradlePlugin.GROUP
            description = 'Prepare server ro run. Configure server and copy compiled plugin to plugins dir'
        } as PrepareServer

        Path runConfigurationsDir = project.projectDir.toPath().resolve(".idea/runConfigurations")
        project.task("buildIdeaRun", dependsOn: "prepareServer").doLast {
            if (Files.notExists(runConfigurationsDir.parent)) {
                throw new LifecycleExecutionException("This task only for IntelliJ IDEA.")
            }

            Files.createDirectories(runConfigurationsDir)
            prepareServer.run.buildIdeaConfiguration(runConfigurationsDir)
        }.configure {
            group = BukkitGradlePlugin.GROUP
            description = 'Configure IDEA server run configuration'
        }

        project.afterEvaluate {
            prepareServer.run.buildIdeaConfiguration(runConfigurationsDir)
        }
    }
}
