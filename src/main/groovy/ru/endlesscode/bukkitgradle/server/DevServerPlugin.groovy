package ru.endlesscode.bukkitgradle.server

import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.endlesscode.bukkitgradle.BukkitGradlePlugin

class DevServerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        ServerCore serverCore = new ServerCore(project)
        project.task("runServer", type: RunServer, dependsOn: "prepareServer") {
            core serverCore
        }.configure {
            group = BukkitGradlePlugin.GROUP
            description = 'Run dev server'
        }

        project.task("prepareServer", type: PrepareServer, dependsOn: ["build", "copyServerCore"]) {
            core serverCore
        }.configure {
            group = BukkitGradlePlugin.GROUP
            description = 'Prepare server ro run. Configure server and copy compiled plugin to plugins dir'
        }
    }
}
