package ru.endlesscode.bukkitgradle.meta

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.nio.file.Path

class GenerateMeta extends DefaultTask {
    @Input
    MetaFile metaFile
    @Input
    Path target

    Path getTarget() {
        return this.target ?: temporaryDir.toPath().resolve(MetaFile.NAME)
    }

    /**
     * Writes meta to target file
     * @return
     */
    @TaskAction
    def generateMeta() {
        metaFile.writeTo(getTarget())
    }
}
