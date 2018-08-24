package ru.endlesscode.bukkitgradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar
import ru.endlesscode.bukkitgradle.extension.RunConfiguration
import ru.endlesscode.bukkitgradle.server.ServerCore

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class PrepareServer extends DefaultTask {
    @Input
    ServerCore core

    Closure<Path> serverDir
    RunConfiguration run

    void setCore(ServerCore core) {
        this.core = core
        this.serverDir = { Files.createDirectories(core.serverDir) }
        this.run = project.bukkit.run
    }

    @TaskAction
    void prepareServer() {
        resolveEula()
        resolveOnlineMode()
        copyPluginsToServerDir()
    }

    void resolveEula() {
        Path eulaFile = getServerDir().resolve("eula.txt")
        if (Files.notExists(eulaFile)) {
            Files.createFile(eulaFile)
        }

        Properties properties = new Properties()
        properties.load(eulaFile.newReader())
        properties.setProperty("eula", "${this.run.eula}")
        properties.store(eulaFile.newWriter(), "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).")
    }

    void resolveOnlineMode() {
        Path propsFile = getServerDir().resolve("server.properties")
        if (Files.notExists(propsFile)) {
            Files.createFile(propsFile)
        }

        Properties properties = new Properties()
        properties.load(propsFile.newReader())
        properties.setProperty("online-mode", "${this.run.onlineMode}")
        properties.store(propsFile.newWriter(), "Minecraft server properties")
    }

    void copyPluginsToServerDir() {
        String pluginName = "${project.bukkit.meta.name}.jar"
        List<Path> paths = project.tasks.withType(Jar).collect { jar ->
            if (jar.classifier in ["src", "source", "sources", "javadoc"]) return
            jar.archivePath.toPath()
        }

        Path pluginsDir = getServerDir().resolve("plugins")
        Files.createDirectories(pluginsDir)
        paths.forEach { jar ->
            if (jar == null || Files.notExists(jar)) return
            Files.copy(jar, pluginsDir.resolve(pluginName), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    Path getServerDir() {
        return serverDir.call()
    }
}
