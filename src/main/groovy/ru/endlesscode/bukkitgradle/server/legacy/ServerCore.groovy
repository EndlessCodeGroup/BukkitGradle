package ru.endlesscode.bukkitgradle.server.legacy

import de.undercouch.gradle.tasks.download.Download
import de.undercouch.gradle.tasks.download.DownloadExtension
import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.StopExecutionException
import ru.endlesscode.bukkitgradle.BukkitExtension
import ru.endlesscode.bukkitgradle.BukkitGradlePlugin
import ru.endlesscode.bukkitgradle.server.PaperConstants
import ru.endlesscode.bukkitgradle.server.ServerConstants
import ru.endlesscode.bukkitgradle.server.ServerProperties
import ru.endlesscode.bukkitgradle.server.extension.CoreType
import ru.endlesscode.bukkitgradle.server.legacy.util.MavenApi

import javax.annotation.Nullable

class ServerCore {

    private final Project project

    private File bukkitGradleDir
    private ServerProperties serverProperties
    private String coreVersion

    private Closure<CoreType> getCoreType = { project.bukkit.server.coreType }

    ServerCore(
            Project project,
            ServerProperties serverProperties,
            File bukkitGradleDir,
            String version
    ) {
        this.project = project
        this.serverProperties = serverProperties
        this.bukkitGradleDir = bukkitGradleDir
        this.coreVersion = version

        MavenApi.init(project)
    }

    /**
     * Registers needed tasks
     */
    void registerTasks() {
        registerDownloadPaperclipTask()
        registerCoreCopyTask()
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

            src resolvePaperclipUrl()
            dest bukkitGradleDir
            onlyIfModified true
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
                if (getCoreType.call() == CoreType.SPIGOT) {
                    srcDir = MavenApi.getSpigotDir(fullVersion)
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
     * Returns server directory
     *
     * @return Server directory or null if dev server location not defined
     */
    @Nullable
    File getServerDir() {
        return serverProperties.devServerDir?.with { new File(it, coreVersion) }
    }

    private String resolvePaperclipUrl() {
        def versionsFile = new File(bukkitGradleDir, PaperConstants.FILE_PAPER_VERSIONS)
        if (!versionsFile.isFile()) {
            project.logger.warn("""
                    Paper versions file not downloaded, make sure that Gradle isn\'t running in offline mode.
            """.stripIndent())
            throw new StopExecutionException()
        }

        def object = new JsonSlurper().parse(versionsFile)

        def versionsUrls = object.versions as Map
        def versionUrl = versionsUrls."$coreVersion"
        if (versionUrl == null) {
            project.logger.warn(
                    "Paper v$coreVersion not found.\n" +
                            "Supported paper versions: ${versionsUrls.keySet()}."
            )
            throw new StopExecutionException()
        }

        return versionUrl
    }

    private String getFullVersion() {
        return coreVersion + BukkitExtension.REVISION_SUFFIX
    }
}
