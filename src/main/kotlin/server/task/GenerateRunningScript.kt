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
import org.gradle.internal.os.OperatingSystem
import org.gradle.kotlin.dsl.property
import ru.endlesscode.bukkitgradle.server.ServerConstants
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
    public var osName: String = System.getProperty("os.name")

    @Internal
    public val scriptDir: DirectoryProperty = objects.directoryProperty()

    @OutputFile
    public val scriptFile: Provider<RegularFile>

    private val scriptStrategy = RunningScriptStrategy.get(OperatingSystem.forName(osName))

    init {
        group = TASK_GROUP
        description = "Generates script to run server without IDE."

        coreFileName.convention(ServerConstants.FILE_CORE)
        scriptFile = scriptDir.map { it.file(scriptStrategy.fileName) }
    }

    @TaskAction
    internal fun generateScript() {
        val scriptFile = scriptFile.get().asFile
        if (!scriptFile.exists()) {
            scriptFile.createNewFile()
        }

        scriptFile.writeText(
            scriptStrategy.getScriptText(
                jvmArgs = jvmArgs.get(),
                coreFileName = coreFileName.get(),
                bukkitArgs = bukkitArgs.get()
            )
        )
    }
}
