package ru.endlesscode.bukkitgradle.server

import de.undercouch.gradle.tasks.download.DownloadExtension
import org.gradle.api.Project
import ru.endlesscode.bukkitgradle.extension.Bukkit

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class ServerCore {
    private static final String MAVEN_METADATA = "maven-metadata.xml"
    private static final String CORE_NAME = "core.jar"

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
        registerUpdateMetaTask()
        registerDownloadingTask()
        registerCoreCopyTask()
    }

    /**
     * Registers updating server core metadata task
     */
    void registerUpdateMetaTask() {
        def task = project.task("updateServerCoreMetadata")
        task.extensions.create("download", DownloadExtension, project)

        task.doLast {
            download {
                src "https://hub.spigotmc.org/nexus/content/repositories/snapshots/org/bukkit/bukkit/$MAVEN_METADATA"
                dest downloadDir.toFile()
                quiet true
            }
        }
    }

    /**
     * Registers core downloading task
     */
    void registerDownloadingTask() {
        def task = project.task("downloadServerCore", dependsOn: "updateServerCoreMetadata")
        task.extensions.create("download", DownloadExtension, project)

        task.doLast {
            download {
                src { "https://yivesmirror.com/files/spigot/${getCoreName()}" }
                dest downloadDir.toFile()
                onlyIfNewer true
            }
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

    Path getServerDir() {
        Path serverDir = this.project.bukkit.run.dir.resolve(getSimpleVersion())
        Files.createDirectories(serverDir)

        return serverDir
    }
}