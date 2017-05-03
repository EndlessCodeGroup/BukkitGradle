package ru.endlesscode.bukkitgradle.server

import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.endlesscode.bukkitgradle.BukkitGradlePlugin

import java.nio.file.Files
import java.nio.file.Paths

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

        project.task("buildIdeaRun", dependsOn: "prepareServer") {
            prepareServer.run.buildIdeaConfiguration(Paths.get(".idea/runConfigurations"))
        }.doLast {
            def runConfigurationsDir = Paths.get(".idea/runConfigurations")
            Files.createDirectories(runConfigurationsDir)
            prepareServer.run.buildIdeaConfiguration(runConfigurationsDir)
        }.configure {
            group = BukkitGradlePlugin.GROUP
            description = 'Configure IDEA server run configuration'
        }
    }
}
