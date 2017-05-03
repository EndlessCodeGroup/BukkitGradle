package ru.endlesscode.bukkitgradle.server

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import ru.endlesscode.bukkitgradle.extension.RunConfiguration

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class PrepareServer extends DefaultTask {
    @Input
    ServerCore core

    Path serverDir
    RunConfiguration run

    void setCore(ServerCore core) {
        this.core = core
        this.serverDir = core.serverDir
        this.run = project.bukkit.run
    }

    @TaskAction
    void prepareServer() {
        resolveEula()
        resolveOnlineMode()
        copyPluginToServerDir()
    }

    void resolveEula() {
        Path eulaFile = serverDir.resolve("eula.txt")
        if (!Files.exists(eulaFile)) {
            Files.createFile(eulaFile)
        }

        Properties properties = new Properties()
        properties.load(eulaFile.newReader())
        properties.setProperty("eula", "${this.run.eula}")
        properties.store(eulaFile.newWriter(), "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).")
    }

    void resolveOnlineMode() {
        Path propsFile = serverDir.resolve("server.properties")
        if (!Files.exists(propsFile)) {
            Files.createFile(propsFile)
        }

        Properties properties = new Properties()
        properties.load(propsFile.newReader())
        properties.setProperty("online-mode", "${this.run.onlineMode}")
        properties.store(propsFile.newWriter(), "Minecraft server properties")
    }

    void copyPluginToServerDir() {
        String pluginName = "${project.bukkit.meta.name}.jar"
        Path jar = project.jar.archivePath.toPath()
        if (!Files.exists(jar)) {
            return
        }
        
        Path pluginsDir = serverDir.resolve("plugins")
        Files.createDirectories(pluginsDir)
        Files.copy(jar, pluginsDir.resolve(pluginName), StandardCopyOption.REPLACE_EXISTING)
    }
}
