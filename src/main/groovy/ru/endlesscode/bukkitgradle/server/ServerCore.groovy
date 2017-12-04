package ru.endlesscode.bukkitgradle.server

import de.undercouch.gradle.tasks.download.DownloadExtension
import org.gradle.api.Project
import org.gradle.internal.impldep.org.apache.maven.lifecycle.LifecycleExecutionException
import ru.endlesscode.bukkitgradle.BukkitGradlePlugin
import ru.endlesscode.bukkitgradle.extension.Bukkit
import ru.endlesscode.bukkitgradle.util.MavenApi

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class ServerCore {
    public static final String CORE_NAME = "core.jar"

    private static final String MAVEN_METADATA = "maven-metadata.xml"

    private final Project project

    private Path bukkitGradleDir

    ServerCore(Project project) {
        this.project = project

        MavenApi.init(project)

        this.initDir()
        this.registerTasks()
    }

    /**
     * Initializes Bukkit Gradle dir
     */
    void initDir() {
        this.bukkitGradleDir = project.buildDir.toPath().resolve("bukkit-gradle")
        Files.createDirectories(bukkitGradleDir)
    }

    /**
     * Registers needed tasks
     */
    void registerTasks() {
        registerBukkitMetaTask()
        registerCoreCopyTask()
    }

    /**
     * Registers Bukkit metadata downloading task
     */
    void registerBukkitMetaTask() {
        project.task("downloadBukkitMeta") {
            def skip = project.gradle.startParameter.isOffline() || BukkitGradlePlugin.isTesting()
            onlyIf { !skip }

            if (skip) {
                return
            }

            extensions.create("download", DownloadExtension, project)

            download {
                src "https://hub.spigotmc.org/nexus/content/repositories/snapshots/org/bukkit/bukkit/$MAVEN_METADATA"
                dest bukkitGradleDir.toFile()
                quiet true
            }
        }.configure {
            group = BukkitGradlePlugin.GROUP
            description = 'Download Bukkit metadata'
        }
    }

    /**
     * Registers core copying task
     */
    void registerCoreCopyTask() {
        project.with {
            task("copyServerCore", dependsOn: "downloadBukkitMeta").doLast {
                Path source = bukkitGradleDir.resolve(getCoreName())
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

        Path metaFile = bukkitGradleDir.resolve(MAVEN_METADATA)
        if (Files.notExists(metaFile)) {
            if (BukkitGradlePlugin.isTesting()) return '1.11.0'

            throw new LifecycleExecutionException(
                    'Server cores meta not downloaded, make sure that Gradle ' +
                            'isn\'t running in offline mode.'
            )
        }

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
