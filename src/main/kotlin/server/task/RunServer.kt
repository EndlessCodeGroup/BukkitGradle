package ru.endlesscode.bukkitgradle.server.task

import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import org.gradle.process.CommandLineArgumentProvider
import ru.endlesscode.bukkitgradle.TASKS_GROUP_BUKKIT
import ru.endlesscode.bukkitgradle.server.ServerConstants

public open class RunServer : JavaExec() {

    @Internal
    public var bukkitArgs: List<String> = emptyList()

    init {
        group = TASKS_GROUP_BUKKIT
        description = "Run dev server."

        mainClass.set("-jar")
        argumentProviders.add(RunServerArgumentsProvider())
        standardInput = System.`in`
    }

    private inner class RunServerArgumentsProvider : CommandLineArgumentProvider {
        override fun asArguments(): Iterable<String> {
            return listOf(ServerConstants.FILE_CORE) + bukkitArgs
        }
    }
}
