package ru.endlesscode.bukkitgradle.server

import de.undercouch.gradle.tasks.download.DownloadExtension
import org.gradle.api.Project
import ru.endlesscode.bukkitgradle.BukkitGradlePlugin
import ru.endlesscode.bukkitgradle.extension.Bukkit

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class ServerCore {
    public static final String CORE_NAME = "core.jar"

    private static final String MAVEN_METADATA = "maven-metadata.xml"

    private final Project project

    private Path downloadDir

    ServerCore(Project project) {
        this.project = project

        this.initDownloadDir()
        this.registerTasks()
    }

    /**
     * Initializes downloading dir
     */
    void initDownloadDir() {
        this.downloadDir = project.buildDir.toPath().resolve("serverCore")
        Files.createDirectories(downloadDir)
    }

    /**
     * Registers needed tasks
     */
    void registerTasks() {
        registerDownloadingTask()
        registerCoreCopyTask()
    }

    /**
     * Registers core downloading task
     */
    void registerDownloadingTask() {
        project.task("downloadServerCore") {
            def skip = project.gradle.startParameter.isOffline() || System.properties['test'] == 'true'
            onlyIf { !skip }

            if (skip) {
                return
            }

            extensions.create("download", DownloadExtension, project)

            download {
                src "https://hub.spigotmc.org/nexus/content/repositories/snapshots/org/bukkit/bukkit/$MAVEN_METADATA"
                dest downloadDir.toFile()
                quiet true
            }

            doLast {
                download {
                    src "https://yivesmirror.com/files/spigot/${getCoreName()}"
                    dest downloadDir.toFile()
                    onlyIfNewer true
                }
            }
        }.configure {
            group = BukkitGradlePlugin.GROUP
            description = 'Download Spigot server core'
        }
    }

    /**
     * Registers core copying task
     */
    void registerCoreCopyTask() {
        project.with {
            task("copyServerCore", dependsOn: "downloadServerCore").doLast {
                Path source = downloadDir.resolve(getCoreName())
                Path destination = getServerDir().resolve(CORE_NAME)

                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING)
            }.configure {
                group = BukkitGradlePlugin.GROUP
                description = 'Copy downloaded server core to server directory'
            }
        }
    }

    /**
     * Returns core file name
     *
     * @return Name of file
     */
    String getCoreName() {
        return "spigot-${getRealVersion()}.jar"
    }

    /**
     * Returns version without revision suffix
     *
     * @return Simple version
     */
    String getSimpleVersion() {
        getRealVersion().replace(Bukkit.REVISION_SUFFIX, "")
    }

    /**
     * Resolves and returns dynamic version
     *
     * @return Real Bukkit version
     */
    private String getRealVersion() {
        String version = project.bukkit.version
        if (version != Bukkit.DYNAMIC_LATEST) {
            return version
        }

        Path metaFile = downloadDir.resolve(MAVEN_METADATA)
        def metadata = new XmlSlurper().parse(metaFile.toFile())
        metadata.versioning.latest.toString()
    }

    /**
     * Returns server directory
     *
     * @return Server directory
     */
    Path getServerDir() {
        Path serverDir = this.project.bukkit.run.dir.resolve(getSimpleVersion())
        Files.createDirectories(serverDir)

        return serverDir
    }
}