package ru.endlesscode.bukkitgradle.server

import org.gradle.api.Plugin
import org.gradle.api.Project

class TestServerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        ServerCore serverCore = new ServerCore(project)
        project.task("prepareServer", type: PrepareServer, dependsOn: ["build", "copyServerCore"]) {
            serverDir = serverCore.serverDir
        }

        project.task("runServer", type: RunServer, dependsOn: "prepareServer") {
            core serverCore
        }
    }
}
