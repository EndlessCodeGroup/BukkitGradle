package ru.endlesscode.bukkitgradle.server.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import ru.endlesscode.bukkitgradle.TASKS_GROUP_BUKKIT
import java.util.*
import javax.inject.Inject

internal open class PrepareServer @Inject constructor(objects: ObjectFactory) : DefaultTask() {

    @Internal
    val serverDir: DirectoryProperty = objects.directoryProperty()

    @Input
    var eula: Boolean = false

    @Input
    var onlineMode: Boolean = true

    @OutputFile
    val eulaFile: Provider<RegularFile> = serverDir.map { it.file("eula.txt") }

    @OutputFile
    val propertiesFile: Provider<RegularFile> = serverDir.map { it.file("server.properties") }

    init {
        group = TASKS_GROUP_BUKKIT
        description = "Prepare server to run. Configure eula and online-mode."
    }

    @TaskAction
    fun prepareServer() {
        resolveEula()
        resolveOnlineMode()
    }

    private fun resolveEula() {
        val eulaFile = eulaFile.get().asFile
        if (!eulaFile.exists()) {
            eulaFile.createNewFile()
        }

        val properties = Properties()
        properties.load(eulaFile.reader())
        properties.setProperty("eula", "$eula")
        properties.store(
            eulaFile.writer(),
            "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula)."
        )
    }

    private fun resolveOnlineMode() {
        val propsFile = propertiesFile.get().asFile
        if (!propsFile.exists()) {
            propsFile.createNewFile()
        }

        val properties = Properties()
        properties.load(propsFile.reader())
        properties.setProperty("online-mode", "$onlineMode")
        properties.store(propsFile.writer(), "Minecraft server properties")
    }
}
