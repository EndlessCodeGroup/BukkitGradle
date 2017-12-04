package ru.endlesscode.bukkitgradle.server

import de.undercouch.gradle.tasks.download.DownloadExtension
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec
import org.gradle.internal.impldep.org.apache.maven.lifecycle.LifecycleExecutionException
import ru.endlesscode.bukkitgradle.BukkitGradlePlugin
import ru.endlesscode.bukkitgradle.extension.Bukkit
import ru.endlesscode.bukkitgradle.util.MavenApi

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ServerCore {
    public static final String CORE_NAME = "core.jar"

    private static final String MAVEN_METADATA = "maven-metadata.xml"

    private final Project project

    private Path bukkitGradleDir
    private boolean forceRebuild = false

    ServerCore(Project project) {
        this.project = project

        MavenApi.init(project)

        this.initDir()

        project.afterEvaluate {
            this.registerTasks()
        }
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
        registerBuildServerCoreTask()
    }

    /**
     * Registers Bukkit metadata downloading task
     */
    void registerBukkitMetaTask() {
        project.task('downloadBukkitMeta') {
            group = BukkitGradlePlugin.GROUP
            description = 'Download Bukkit metadata'

            def skip = project.gradle.startParameter.isOffline() || BukkitGradlePlugin.isTesting()
            onlyIf { !skip }
            if (skip) return

            extensions.create("download", DownloadExtension, project)

            download {
                src "https://hub.spigotmc.org/nexus/content/repositories/snapshots/org/bukkit/bukkit/$MAVEN_METADATA"
                dest bukkitGradleDir.toFile()
                quiet true
            }
        }
    }

    /**
     * Registers core copying task
     */
    void registerCoreCopyTask() {
        project.with {
            task('copyServerCore', type: Copy,
                    dependsOn: ['buildServerCore']) {
                group = BukkitGradlePlugin.GROUP
                description = 'Copy built server core to server directory'

                def coreName = getCoreName()
                from MavenApi.getSpigotDir(realVersion)
                include coreName
                rename(coreName, CORE_NAME)
                into getServerDir().toString()
            }
        }
    }

    /**
     * Registers core building task
     */
    void registerBuildServerCoreTask() {
        project.task('buildServerCore', type: JavaExec, dependsOn: 'downloadBukkitMeta') {
            group = BukkitGradlePlugin.GROUP
            description = 'Build server core'

            onlyIf {
                if (forceRebuild) {
                    forceRebuild = false
                    return true
                }

                return !MavenApi.hasSpigot(getRealVersion())
            }

            def path = Paths.get(project.bukkit.buildtools as String)
            def absolutePath = path.toAbsolutePath().toString()
            if (Files.notExists(path) || !Files.isRegularFile(path)) {
                project.logger.warn("BuildTools not found on path: '$absolutePath'\n" +
                        'It should be path to .jar file of BuildTools.')
                enabled = false
                return
            }

            main = '-jar'
            args absolutePath, '--rev', getSimpleVersion()
            workingDir = path.getParent().toAbsolutePath().toString()
            standardInput = System.in
        }

        project.task('rebuildServerCore') {
            group = BukkitGradlePlugin.GROUP
            description = 'Force rebuild server core'
        }.doLast {
            forceRebuild = true
        }.finalizedBy project.tasks.buildServerCore
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
        return getRealVersion().replace(Bukkit.REVISION_SUFFIX, '')
    }

    /**
     * Resolves and returns dynamic version
     *
     * @return Real Bukkit version
     */
    private String getRealVersion() {
        String version = project.bukkit.version
        if (version != Bukkit.LATEST) {
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
        return metadata.versioning.latest.toString()
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
