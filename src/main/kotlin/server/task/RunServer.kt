package ru.endlesscode.bukkitgradle.server.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.os.OperatingSystem
import org.gradle.kotlin.dsl.property
import ru.endlesscode.bukkitgradle.TASKS_GROUP_BUKKIT
import ru.endlesscode.bukkitgradle.server.script.RunningScriptStrategy
import javax.inject.Inject

/** Run dev server from script. */
public open class RunServer @Inject constructor(objects: ObjectFactory) : DefaultTask() {

    @Internal
    public val title: Property<String> = objects.property()

    @Input
    public val scriptFile: RegularFileProperty = objects.fileProperty()

    @Internal
    public var osName: String = System.getProperty("os.name")

    private val scriptStrategy = RunningScriptStrategy.get(OperatingSystem.forName(osName))

    init {
        group = TASKS_GROUP_BUKKIT
        description = "Run dev server from script."

        title.convention("Dev Server")
    }

    @TaskAction
    internal fun runServer() {
        logger.lifecycle("Starting Server...")
        ProcessBuilder(scriptStrategy.getCommand(title.get()))
            .redirectErrorStream(true)
            .directory(scriptFile.get().asFile.parentFile)
            .start()
        logger.lifecycle("Server started successfully!")
    }
}
