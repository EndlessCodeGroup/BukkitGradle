package ru.endlesscode.bukkitgradle.server.task

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.OutputFile
import org.gradle.process.CommandLineArgumentProvider
import ru.endlesscode.bukkitgradle.TASKS_GROUP_BUKKIT
import java.io.File

@Suppress("LeakingThis")
public abstract class BuildServerCore : JavaExec() {

    @get:InputFile
    public abstract val buildToolsFile: Property<File>

    @get:Input
    public abstract val version: Property<String>

    @OutputFile
    public val spigotFile: Provider<File> = buildToolsFile.zip(version) { buildToolsFile, version ->
        File(buildToolsFile.parentFile, "spigot-$version.jar")
    }

    init {
        group = TASKS_GROUP_BUKKIT
        description = "Build server core"

        mainClass.set("-jar")
        argumentProviders.add(BuildToolsArgumentsProvider())
        standardInput = System.`in`

        outputs.upToDateWhen { spigotFile.get().isFile }
    }

    private inner class BuildToolsArgumentsProvider : CommandLineArgumentProvider {

        override fun asArguments(): Iterable<String> {
            val buildToolsPath = buildToolsFile.get().path
            return listOf(buildToolsPath, "--rev", version.get())
        }
    }
}
