package ru.endlesscode.bukkitgradle.server.legacy

import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import ru.endlesscode.bukkitgradle.Bukkit
import ru.endlesscode.bukkitgradle.BukkitGradlePlugin
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta
import ru.endlesscode.bukkitgradle.server.BuildToolsConstants
import ru.endlesscode.bukkitgradle.server.PaperConstants
import ru.endlesscode.bukkitgradle.server.ServerConstants
import ru.endlesscode.bukkitgradle.server.ServerProperties
import ru.endlesscode.bukkitgradle.server.extension.CoreType
import ru.endlesscode.bukkitgradle.server.extension.ServerConfiguration
import ru.endlesscode.bukkitgradle.server.task.*

class LegacyDevServerPlugin implements Plugin<Project> {

    private Project project
    private Bukkit bukkit
    private File bukkitGradleDir

    @Override
    void apply(Project project) {
        this.project = project
        bukkit = project.bukkit as Bukkit

        bukkitGradleDir = new File(project.buildDir, "bukkit-gradle")
        bukkitGradleDir.mkdirs()

        ServerProperties properties = new ServerProperties(project.rootDir)
        // FIXME: Should be calculated on task configuration
        def coreVersion = serverConfiguration.version ?: bukkit.apiVersion
        ServerCore serverCore = new ServerCore(project, properties, coreVersion)

        // Register tasks
        def buildServerCore = registerBuildServerCoreTask(properties.buildToolsDir, coreVersion)
        def downloadPaperclip = registerDownloadPaperclip(coreVersion)
        registerCopyServerCoreTask(buildServerCore, downloadPaperclip, serverCore.serverDir)

        def generateRunningScript = registerGenerateRunningScriptTask(serverCore.serverDir)
        def prepareServer = registerPrepareServerTask(serverCore.serverDir)
        registerRunServerTask(generateRunningScript, prepareServer)

        registerBuildIdeRunTask(serverCore.serverDir)
    }

    private TaskProvider<BuildServerCore> registerBuildServerCoreTask(File buildToolsDir, String coreVersion) {
        def downloadBuildTools = tasks.register('downloadBuildTools', Download) {
            group = BukkitGradlePlugin.GROUP
            description = 'Download BuildTools'

            src(BuildToolsConstants.URL)
            dest(buildToolsDir)
            onlyIfModified(true)
        }

        return tasks.register('buildServerCore', BuildServerCore) {
            buildToolsFile.set(downloadBuildTools.map { it.outputFiles.first() })
            version.set(coreVersion)
        }
    }

    private TaskProvider<DownloadPaperclip> registerDownloadPaperclip(String coreVersion) {
        def downloadPaperVersions = tasks.register('downloadPaperVersions', Download) {
            src(PaperConstants.URL_PAPER_VERSIONS)
            dest(bukkitGradleDir)
            quiet(true)
            onlyIfModified(true)
        }

        return tasks.register('downloadPaperclip', DownloadPaperclip) {
            paperVersionsFile.set(downloadPaperVersions.map { it.outputFiles.first() })
            version.set(coreVersion)
            dest(bukkitGradleDir)
        }
    }

    private TaskProvider<GenerateRunningScript> registerGenerateRunningScriptTask(File serverDir) {
        return project.tasks.register('generateRunningScript', GenerateRunningScript) {
            jvmArgs.set(serverConfiguration.buildJvmArgs())
            bukkitArgs.set(serverConfiguration.bukkitArgs)
            scriptDir.set(serverDir)
        }
    }

    private def registerCopyServerCoreTask(
            TaskProvider<BuildServerCore> buildServerCore,
            TaskProvider<DownloadPaperclip> downloadPaperclip,
            File serverDir
    ) {
        project.register('copyServerCore', Copy) {
            group = BukkitGradlePlugin.GROUP
            description = 'Copy server core to server directory'

            Provider<File> source
            if (serverConfiguration.coreType == CoreType.SPIGOT) {
                source = buildServerCore.map { it.buildToolsFile.get() }
            } else {
                source = downloadPaperclip.map { it.paperclipFile.get() }
            }

            from(source.map { it.parentFile })
            include(source.get().name) // FIXME: Check if it works properly
            rename { ServerConstants.FILE_CORE }
            into(serverDir)
        }
    }

    private TaskProvider<PrepareServer> registerPrepareServerTask(File serverDir) {
        def jarTaskName = project.plugins.hasPlugin("com.github.johnrengelman.shadow") ? "shadowJar" : "jar"
        def jarTask = tasks.named(jarTaskName, Jar)
        def copyPlugins = tasks.register("copyPlugins", Copy) {
            from(jarTask)
            into(project.mkdir(new File(serverDir, "plugins")))
            rename { "${pluginMeta.name.get()}.jar" }
        }

        return tasks.register('prepareServer', PrepareServer) {
            it.serverDir.set(serverDir)
            eula = serverConfiguration.eula
            onlineMode = serverConfiguration.onlineMode
            dependsOn('copyServerCore', copyPlugins)
        }
    }

    private def registerRunServerTask(
            TaskProvider<GenerateRunningScript> generateRunningScript,
            TaskProvider<PrepareServer> prepareServer
    ) {
        tasks.register('runServer', RunServer) {
            scriptFile.set(generateRunningScript.map { it.scriptFile.get().asFile })
            dependsOn(prepareServer)
        }
    }

    private def registerBuildIdeRunTask(File serverDir) {
        tasks.register('buildIdeaRun', CreateIdeaJarRunConfiguration) {
            configurationName.set("$project.name: Run server")
            beforeRunTask.set('prepareServer')
            configurationsDir.set(project.rootProject.layout.projectDirectory.dir('.idea/runConfigurations'))
            jarPath.set(new File(serverDir, ServerConstants.FILE_CORE))
        }
    }

    private ServerConfiguration getServerConfiguration() {
        return bukkit.server
    }

    private PluginMeta getPluginMeta() {
        return bukkit.meta
    }

    private TaskContainer getTasks() {
        return project.tasks
    }
}
