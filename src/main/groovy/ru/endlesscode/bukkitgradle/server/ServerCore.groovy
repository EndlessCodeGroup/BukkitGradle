package ru.endlesscode.bukkitgradle.server

import de.undercouch.gradle.tasks.download.DownloadExtension
import org.gradle.api.Project
import org.gradle.api.Task
import ru.endlesscode.bukkitgradle.extension.Bukkit

import java.nio.file.Files
import java.nio.file.Path

class ServerCore {
    private static final String MAVEN_METADATA = "maven-metadata.xml"

    private final Project project

    private Path downloadDir

    ServerCore(Project project) {
        this.project = project

        this.initDownloadDir()
        this.registerTasks()
    }

    void initDownloadDir() {
        this.downloadDir = project.buildDir.toPath().resolve("server")
        Files.createDirectories(downloadDir)
    }

    void registerTasks() {
        registerUpdateMetaTask()
        registerDownloadingTask()
    }

    Task registerUpdateMetaTask() {
        def task = project.task("updateServerCoreMetadata")
        task.extensions.create("download", DownloadExtension, project)

        task.doLast {
            println "Meta"
            download {
                src "https://hub.spigotmc.org/nexus/content/repositories/snapshots/org/bukkit/bukkit/$MAVEN_METADATA"
                dest downloadDir.toFile()
                quiet true
            }
        }
    }

    Task registerDownloadingTask() {
        def task = project.task("downloadServerCore", dependsOn: "updateServerCoreMetadata")
        task.extensions.create("download", DownloadExtension, project)

        task.doLast {
            download {
                println "Download"
                src { "https://yivesmirror.com/files/spigot/${getCoreName()}" }
                dest downloadDir.toFile()
                onlyIfNewer true
            }
        }
    }

    String getCoreName() {
        return "spigot-${getRealVersion()}.jar"
    }

    private String getRealVersion() {
        String version = project.bukkit.version
        if (version != Bukkit.DYNAMIC_LATEST) {
            return version
        }

        Path metaFile = downloadDir.resolve(MAVEN_METADATA)
        def metadata = new XmlSlurper().parse(metaFile.toFile())

        return metadata.versioning.latest.toString()
    }
}