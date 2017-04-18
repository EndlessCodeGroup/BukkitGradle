package ru.endlesscode.bukkitgradle.extension

import org.gradle.api.Project

class RunConfiguration {
    private Project project

    RunConfiguration(Project project) {
        this.project = project
    }
}
