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

    @TaskAction
    void prepareServer() {
        this.serverDir = core.serverDir
        this.run = project.bukkit.run

        resolveEula()
        resolveOnlineMode()
        copyPluginToServerDir()
    }

    void resolveEula() {
        Path eulaFile = serverDir.resolve("eula.txt")

        boolean eula = this.run.eula
        eulaFile.text = "eula=$eula"
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
        Path pluginsDir = serverDir.resolve("plugins")
        Files.createDirectories(pluginsDir)

        Files.copy(jar, pluginsDir.resolve(pluginName), StandardCopyOption.REPLACE_EXISTING)
    }
}
