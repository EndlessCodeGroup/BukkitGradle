package ru.endlesscode.bukkitgradle.server.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import ru.endlesscode.bukkitgradle.TASKS_GROUP_BUKKIT
import java.util.*

internal abstract class PrepareServer : DefaultTask() {

    @get:Internal
    abstract val serverDir: DirectoryProperty

    @get:Input
    var onlineMode: Boolean = true

    @get:OutputFile
    val propertiesFile: Provider<RegularFile> = serverDir.map { it.file("server.properties") }

    init {
        group = TASKS_GROUP_BUKKIT
        description = "Prepare server to run. Configure eula and online-mode."
    }

    @TaskAction
    fun prepareServer() {
        configureOnlineMode()
    }

    private fun configureOnlineMode() {
        val propsFile = propertiesFile.get().asFile
        if (!propsFile.exists()) {
            propsFile.createNewFile()
        }

        val properties = Properties()
        propsFile.reader().use { properties.load(it) }
        properties.setProperty("online-mode", "$onlineMode")
        propsFile.writer().use { properties.store(it, "Minecraft server properties") }
    }
}
