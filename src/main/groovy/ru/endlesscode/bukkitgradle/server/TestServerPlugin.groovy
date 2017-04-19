package ru.endlesscode.bukkitgradle.server

import org.gradle.api.Plugin
import org.gradle.api.Project

class TestServerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.task("runServer", type: RunServer, dependsOn: "copyServerCore") {
            core new ServerCore(project)
        }
    }
}
