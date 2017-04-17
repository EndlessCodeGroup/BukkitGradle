package ru.endlesscode.gradle.bukkit.server

import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.Project
import ru.endlesscode.gradle.bukkit.extension.Bukkit

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
        registerAndExecuteUpdateMetaTask()
        registerDownloadingTask()
    }

    void registerAndExecuteUpdateMetaTask() {
        project.task("updateServerCoreMetadata", type: Download) {
            src "https://hub.spigotmc.org/nexus/content/repositories/snapshots/org/bukkit/bukkit/$MAVEN_METADATA"
            dest downloadDir.toFile()
            quiet true
            onlyIfNewer true
        }.execute()
    }

    void registerDownloadingTask() {
        project.task("downloadServerCore", type: Download) {
            src "https://yivesmirror.com/files/spigot/${getCoreName()}"
            dest downloadDir.toFile()
            onlyIfNewer true
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