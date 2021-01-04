package ru.endlesscode.bukkitgradle.server.legacy.task

import groovy.transform.Internal
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar
import ru.endlesscode.bukkitgradle.server.extension.RunConfiguration
import ru.endlesscode.bukkitgradle.server.legacy.ServerCore

class PrepareServer extends DefaultTask {
    @Internal
    ServerCore core

    @InputDirectory
    final DirectoryProperty serverDir = project.objects.directoryProperty()

    @Internal
    final Property<Jar> jarTask = project.objects.property(Jar)

    @InputFile
    final Provider<RegularFile> jarFile = jarTask.map {it.archiveFile.get() }

    @Internal
    RunConfiguration run

    PrepareServer() {
        def jarTaskName = project.plugins.hasPlugin('com.github.johnrengelman.shadow') ? 'shadowJar' : 'jar'
        jarTask.convention(project.tasks.named(jarTaskName, Jar))
    }

    void setCore(ServerCore core) {
        this.core = core
        this.serverDir.fileProvider(project.provider { project.mkdir(core.serverDir) })
        this.run = project.bukkit.run
    }

    @TaskAction
    void prepareServer() {
        resolveEula()
        resolveOnlineMode()
        copyPluginsToServerDir()
    }

    void resolveEula() {
        def eulaFile = serverDir.file("eula.txt").get().asFile
        if (!eulaFile.exists()) {
            eulaFile.createNewFile()
        }

        Properties properties = new Properties()
        properties.load(eulaFile.newReader("UTF-8"))
        properties.setProperty("eula", "${this.run.eula}")
        properties.store(eulaFile.newWriter("UTF-8"), "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).")
    }

    void resolveOnlineMode() {
        def propsFile = serverDir.file("server.properties").get().asFile
        if (!propsFile.exists()) {
            propsFile.createNewFile()
        }

        Properties properties = new Properties()
        properties.load(propsFile.newReader("UTF-8"))
        properties.setProperty("online-mode", "${this.run.onlineMode}")
        properties.store(propsFile.newWriter("UTF-8"), "Minecraft server properties")
    }

    void copyPluginsToServerDir() {
        project.copy {
            from(jarTask)
            into(project.mkdir(serverDir.dir('plugins')))
            rename { "${project.bukkit.meta.name}.jar" }
        }
    }
}
