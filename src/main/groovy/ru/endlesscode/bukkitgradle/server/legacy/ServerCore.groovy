package ru.endlesscode.bukkitgradle.server.legacy

import de.undercouch.gradle.tasks.download.Download
import de.undercouch.gradle.tasks.download.DownloadExtension
import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec
import ru.endlesscode.bukkitgradle.BukkitGradlePlugin
import ru.endlesscode.bukkitgradle.BukkitGroovy
import ru.endlesscode.bukkitgradle.server.PaperConstants
import ru.endlesscode.bukkitgradle.server.ServerConstants
import ru.endlesscode.bukkitgradle.server.extension.CoreType
import ru.endlesscode.bukkitgradle.server.legacy.util.MavenApi

import javax.annotation.Nullable

class ServerCore {

    private final Project project

    private File bukkitGradleDir
    private boolean forceRebuild = false
    private ServerProperties serverProperties

    private Closure<CoreType> getCoreType = { project.bukkit.run.coreType }
    private String paperUrl = PaperConstants.URL_PAPER_DEFAULT

    ServerCore(Project project, ServerProperties serverProperties, File bukkitGradleDir) {
        this.project = project
        this.serverProperties = serverProperties
        this.bukkitGradleDir = bukkitGradleDir

        MavenApi.init(project)
    }

    /**
     * Registers needed tasks
     */
    void registerTasks() {
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
                    src ServerConstants.URL_SPIGOT_METADATA
                    dest bukkitGradleDir
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

            src ServerConstants.URL_BUILDTOOLS
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
                    dest bukkitGradleDir
                    quiet true
                    onlyIfModified true
                }
            } catch (Exception e) {
                logger.error("Error on paperclip versions list downloading: ${e.toString()}")
            }

            if (serverDir == null) {
                enabled = false
                return
            }

            src paperUrl
            dest bukkitGradleDir
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

                def buildToolsFile = new File(serverProperties.buildToolsDir, ServerConstants.FILE_BUILDTOOLS)
                if (!buildToolsFile.isFile()) {
                    logger.warn("BuildTools not found on path: '$buildToolsFile'\n" +
                            'BuildTools directory should contain BuildTools.jar file.')
                    enabled = false
                    return
                }

                main = '-jar'
                args(buildToolsFile.path, '--rev', getSimpleVersion())
                workingDir = buildToolsFile.parentFile.path
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

                File srcDir
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
                into serverDir
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
    File getServerDir() {
        return serverProperties.devServerDir?.with { new File(it, simpleVersion) }
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

        if (version != BukkitGroovy.LATEST) {
            return version
        }

        def metaFile = new File(bukkitGradleDir, ServerConstants.FILE_MAVEN_METADATA)
        if (!metaFile.exists()) {
            if (BukkitGradlePlugin.isTesting()) return '1.11.0'

            project.logger.warn(
                    'Server core meta not downloaded, make sure that Gradle ' +
                            'isn\'t running in offline mode.\n' +
                            "Using '$ServerConstants.FALLBACK_VERSION' by default."
            )

            return ServerConstants.FALLBACK_VERSION
        }

        def metadata = new XmlSlurper().parse(metaFile)
        return metadata.versioning.latest.toString()
    }

    private String getPaperCoreVersion() {
        def versionsFile = new File(bukkitGradleDir, PaperConstants.FILE_PAPER_VERSIONS)
        if (!versionsFile.isFile()) {
            project.logger.warn("""
                    Paper versions file not downloaded, make sure that Gradle isn\'t running in offline mode.
                    Using '$PaperConstants.FALLBACK_VERSION' by default.
            """.stripIndent())

            return PaperConstants.FALLBACK_VERSION
        }

        def object = new JsonSlurper().parse(versionsFile)

        String version = simplifyVersion(project.bukkit.version)
        if (version == BukkitGroovy.LATEST) {
            version = object.latest
        }

        def versionsUrls = object.versions as Map
        def versionUrl = versionsUrls."$version"
        if (versionUrl == null) {
            project.logger.warn(
                    "Paper v$version not found.\n" +
                            "Supported paper versions: ${versionsUrls.keySet()}\n" +
                            "Using '$PaperConstants.FALLBACK_VERSION' by default."
            )

            return PaperConstants.FALLBACK_VERSION
        }

        paperUrl = versionUrl
        return version
    }

    private static def simplifyVersion(version) {
        return version.replace(BukkitGroovy.REVISION_SUFFIX, '')
    }
}
