package ru.endlesscode.bukkitgradle.server

import de.undercouch.gradle.tasks.download.Download
import de.undercouch.gradle.tasks.download.DownloadExtension
import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec
import ru.endlesscode.bukkitgradle.BukkitGradlePlugin
import ru.endlesscode.bukkitgradle.extension.Bukkit
import ru.endlesscode.bukkitgradle.util.MavenApi

import javax.annotation.Nullable
import java.nio.file.Files
import java.nio.file.Path

class ServerCore {

    private static final String FALLBACK_VERSION = "1.15.2"
    private static final String PAPER_FALLBACK_VERSION = "1.15.2"

    private final Project project

    private Path bukkitGradleDir
    private boolean forceRebuild = false
    private ServerProperties serverProperties

    private Closure<CoreType> getCoreType = { project.bukkit.run.coreType }
    private String paperUrl = PaperConstants.URL_PAPER_DEFAULT

    ServerCore(Project project) {
        this.project = project
        serverProperties = new ServerProperties(project.rootDir.toPath())

        MavenApi.init(project)
        this.initDir()

        project.afterEvaluate {
            this.registerTasks()
        }
    }

    /**
     * Initializes Bukkit Gradle dir
     */
    private void initDir() {
        this.bukkitGradleDir = project.buildDir.toPath().resolve("bukkit-gradle")
        Files.createDirectories(bukkitGradleDir)
    }

    /**
     * Registers needed tasks
     */
    private void registerTasks() {
        registerBukkitMetaTask()
        registerDownloadBuildToolsTask()
        registerBuildServerCoreTask()
        registerDownloadPaperclipTask()
        registerCoreCopyTask()
    }

    /**
     * Registers Bukkit metadata downloading task
     */
    private void registerBukkitMetaTask() {
        project.task('downloadBukkitMeta') {
            group = BukkitGradlePlugin.GROUP
            description = 'Download Bukkit metadata'

            def skip = project.gradle.startParameter.isOffline() || BukkitGradlePlugin.isTesting()
            onlyIf { !skip }
            if (skip) return

            extensions.create("download", DownloadExtension, project)
            try {
                download {
                    src ServerConstants.URL_BUKKIT_METADATA
                    dest bukkitGradleDir.toFile()
                    quiet true
                }
            } catch (Exception e) {
                logger.error("Error on bukkit meta downloading: ${e.toString()}")
            }
        }
    }

    private void registerDownloadBuildToolsTask() {
        project.task('downloadBuildTools', type: Download) {
            group = BukkitGradlePlugin.GROUP
            description = 'Download BuildTools'

            // Skip it for not spigot
            if (getCoreType() != CoreType.SPIGOT) {
                enabled = false
                return
            }

            def destDir = serverProperties.buildToolsDir
            if (destDir == null) {
                enabled = false
                return
            }

            src "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar"
            dest destDir.toString()
            onlyIfModified true
        }
    }

    private void registerDownloadPaperclipTask() {
        project.task('downloadPaperclip', type: Download) {
            group = BukkitGradlePlugin.GROUP
            description = 'Download paperclip'

            if (project.tasks.downloadBuildTools.enabled) {
                enabled = false
                return
            }

            def skip = project.gradle.startParameter.isOffline() || BukkitGradlePlugin.isTesting()
            onlyIf { !skip }
            if (skip) return

            extensions.create("download", DownloadExtension, project)
            try {
                download {
                    src PaperConstants.URL_PAPER_VERSIONS
                    dest bukkitGradleDir.toFile()
                    quiet true
                    onlyIfModified true
                }
            } catch (Exception e) {
                logger.error("Error on paperclip versions list downloading: ${e.toString()}")
            }

            Path destDir = serverDir
            if (destDir == null) {
                enabled = false
                return
            }

            src paperUrl
            dest bukkitGradleDir.toString()
            onlyIfModified true
        }
    }

    /**
     * Registers core building task
     */
    private void registerBuildServerCoreTask() {
        project.with {
            task('buildServerCore', type: JavaExec, dependsOn: ['downloadBuildTools', 'downloadBukkitMeta']) {
                group = BukkitGradlePlugin.GROUP
                description = 'Build server core, but only if it not contains in local maven repo'

                onlyIf {
                    if (forceRebuild) {
                        forceRebuild = false
                        return true
                    }

                    return !MavenApi.hasSpigot(getCoreVersion())
                }

                if (!tasks.downloadBuildTools.enabled || serverDir == null) {
                    enabled = false
                    return
                }

                def path = serverProperties.buildToolsDir.resolve(ServerConstants.FILE_BUILDTOOLS)
                def absolutePath = path.toAbsolutePath().toString()
                if (Files.notExists(path) || Files.isDirectory(path)) {
                    logger.warn("BuildTools not found on path: '$absolutePath'\n" +
                            'BuildTools directory should contains BuildTools.jar file.')
                    enabled = false
                    return
                }

                main = '-jar'
                args(absolutePath, '--rev', getSimpleVersion())
                workingDir = path.getParent().toAbsolutePath().toString()
                standardInput = System.in
            }

            task('rebuildServerCore') {
                group = BukkitGradlePlugin.GROUP
                description = 'Force rebuild server core'
            }.doLast {
                forceRebuild = true
            }.finalizedBy tasks.buildServerCore
        }
    }

    /**
     * Registers core copying task
     */
    private void registerCoreCopyTask() {
        project.with {
            task('copyServerCore', type: Copy,
                    dependsOn: ['buildServerCore', 'downloadPaperclip']) {
                group = BukkitGradlePlugin.GROUP
                description = 'Copy server core to server directory'

                def srcDir
                def fileName
                if (getCoreType() == CoreType.SPIGOT) {
                    srcDir = MavenApi.getSpigotDir(coreVersion)
                    fileName = getSpigotCoreName()
                } else {
                    srcDir = bukkitGradleDir
                    fileName = PaperConstants.FILE_PAPERCLIP
                }

                from srcDir
                include fileName
                rename(fileName, ServerConstants.FILE_CORE)
                into serverDir.toString()
            }
        }
    }

    /**
     * Returns core file name
     *
     * @return Name of file
     */
    private String getSpigotCoreName() {
        return "spigot-${coreVersion}.jar"
    }

    /**
     * Returns version without revision suffix
     *
     * @return Simple version
     */
    String getSimpleVersion() {
        return simplifyVersion(coreVersion)
    }

    /**
     * Returns server directory
     *
     * @return Server directory or null if dev server location not defined
     */
    @Nullable
    Path getServerDir() {
        return serverProperties.devServerDir?.resolve(simpleVersion)
    }

    /**
     * Resolves and returns dynamic version
     *
     * @return Real Bukkit version
     */
    private String getCoreVersion() {
        switch (getCoreType()) {
            case CoreType.SPIGOT:
                return getSpigotCoreVersion()
            case CoreType.PAPER:
                return getPaperCoreVersion()
        }
    }

    private String getSpigotCoreVersion() {
        String version = project.bukkit.version

        if (version != Bukkit.LATEST) {
            return version
        }

        Path metaFile = bukkitGradleDir.resolve(ServerConstants.FILE_MAVEN_METADATA)
        if (Files.notExists(metaFile)) {
            if (BukkitGradlePlugin.isTesting()) return '1.11.0'

            project.logger.warn(
                    'Server core meta not downloaded, make sure that Gradle ' +
                            'isn\'t running in offline mode.\n' +
                            "Using '$FALLBACK_VERSION' by default."
            )

            return FALLBACK_VERSION
        }

        def metadata = new XmlSlurper().parse(metaFile.toFile())
        return metadata.versioning.latest.toString()
    }

    private String getPaperCoreVersion() {
        Path versionsFile = bukkitGradleDir.resolve(PaperConstants.FILE_PAPER_VERSIONS)
        if (Files.notExists(versionsFile)) {
            project.logger.warn(
                    'Paper versions file not downloaded, make sure that Gradle ' +
                            'isn\'t running in offline mode.\n' +
                            "Using '$PAPER_FALLBACK_VERSION' by default."
            )

            return PAPER_FALLBACK_VERSION
        }

        def object = new JsonSlurper().parse(versionsFile.toFile())

        String version = simplifyVersion(project.bukkit.version)
        if (version == Bukkit.LATEST) {
            version = object.latest
        }

        def versionsUrls = object.versions as Map
        def versionUrl = versionsUrls."$version"
        if (versionUrl == null) {
            project.logger.warn(
                    "Paper v$version not found.\n" +
                            "Supported paper versions: ${versionsUrls.keySet()}\n" +
                            "Using '$FALLBACK_VERSION' by default."
            )

            return FALLBACK_VERSION
        }

        paperUrl = versionUrl
        return version
    }

    private static def simplifyVersion(version) {
        return version.replace(Bukkit.REVISION_SUFFIX, '')
    }
}
