package ru.endlesscode.bukkitgradle.meta.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import ru.endlesscode.bukkitgradle.meta.MetaFile

class GenerateMeta extends DefaultTask {

    @Input
    final Property<MetaFile> metaFile = project.objects.property(MetaFile)

    @OutputFile
    final RegularFileProperty target = project.objects.fileProperty()

    GenerateMeta() {
        def defaultTargetProvider = project.provider { new File(temporaryDir, MetaFile.NAME) }
        target.convention(project.layout.file(defaultTargetProvider))
    }

    /**
     * Writes meta to target file
     * @return
     */
    @TaskAction
    def generateMeta() {
        metaFile.get().writeTo(target.asFile.get().toPath())
    }
}
