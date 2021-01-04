package ru.endlesscode.bukkitgradle.server.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property
import ru.endlesscode.bukkitgradle.server.script.RunningScriptStrategy
import javax.inject.Inject

/** Generates script to run server without IDE. */
public open class GenerateRunningScript @Inject constructor(objects: ObjectFactory) : DefaultTask() {

    @Input
    public val jvmArgs: Property<String> = objects.property()

    @Input
    public val bukkitArgs: Property<String> = objects.property()

    @Input
    public val coreFileName: Property<String> = objects.property()

    @Input
    public val scriptStrategy: Property<RunningScriptStrategy> =
        objects.property<RunningScriptStrategy>().convention(RunningScriptStrategy.get())

    @Internal
    public val scriptDir: DirectoryProperty = objects.directoryProperty()

    @OutputFile
    public val scriptFile: Provider<RegularFile> = scriptDir.zip(scriptStrategy) { scriptDir, strategy ->
        scriptDir.file(strategy.fileName)
    }

    init {
        group = TASK_GROUP
        description = "Generates script to run server without IDE."
    }

    @TaskAction
    internal fun generateScript() {
        val scriptFile = scriptFile.get().asFile
        if (!scriptFile.exists()) {
            scriptFile.createNewFile()
        }

        scriptFile.writeText(
            scriptStrategy.get().getScriptText(
                jvmArgs = jvmArgs.get(),
                coreFileName = coreFileName.get(),
                bukkitArgs = bukkitArgs.get()
            )
        )
    }
}
