package ru.endlesscode.bukkitgradle.server

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.nio.file.Path

class PrepareServer extends DefaultTask {
    @Input
    Path serverDir

    @TaskAction
    void copyPlugins() {

    }
}
