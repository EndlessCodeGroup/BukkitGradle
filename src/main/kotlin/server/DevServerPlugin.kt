package ru.endlesscode.bukkitgradle.server

import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import ru.endlesscode.bukkitgradle.Bukkit
import ru.endlesscode.bukkitgradle.TASKS_GROUP_BUKKIT
import ru.endlesscode.bukkitgradle.bukkit
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta
import ru.endlesscode.bukkitgradle.server.extension.CoreType
import ru.endlesscode.bukkitgradle.server.extension.ServerConfiguration
import ru.endlesscode.bukkitgradle.server.task.*
import java.io.File

public class DevServerPlugin : Plugin<Project> {

    private lateinit var project: Project
    private lateinit var bukkit: Bukkit
    private lateinit var bukkitGradleDir: File

    private val serverConfiguration: ServerConfiguration
        get() = bukkit.server

    private val pluginMeta: PluginMeta
        get() = bukkit.meta

    private val tasks: TaskContainer
        get() = project.tasks

    @Override
    override fun apply(target: Project) {
        project = target
        bukkit = project.bukkit

        bukkitGradleDir = File(project.buildDir, "bukkit-gradle")
        bukkitGradleDir.mkdirs()

        val properties = ServerProperties(project.rootDir)
        // FIXME: Should be calculated on task configuration
        val coreVersion = serverConfiguration.version ?: bukkit.apiVersion
        val serverDir = File(properties.devServerDir, coreVersion)

        // Register tasks
        val buildServerCore = registerBuildServerCoreTask(properties.buildToolsDir, coreVersion)
        val downloadPaperclip = registerDownloadPaperclip(coreVersion)
        val copyServerCore = registerCopyServerCoreTask(buildServerCore, downloadPaperclip, serverDir)

        val generateRunningScript = registerGenerateRunningScriptTask(serverDir)
        val prepareServer = registerPrepareServerTask(copyServerCore, serverDir)
        registerRunServerTask(generateRunningScript, prepareServer)

        registerBuildIdeRunTask(serverDir)
    }

    private fun registerBuildServerCoreTask(buildToolsDir: File, coreVersion: String): TaskProvider<BuildServerCore> {
        val downloadBuildTools = tasks.register<Download>("downloadBuildTools") {
            group = TASKS_GROUP_BUKKIT
            description = "Download BuildTools"

            src(BuildToolsConstants.URL)
            dest(buildToolsDir)
            onlyIfModified(true)
        }

        return tasks.register<BuildServerCore>("buildServerCore") {
            buildToolsFile.set(downloadBuildTools.map { it.outputFiles.single() })
            version.set(coreVersion)
        }
    }

    private fun registerDownloadPaperclip(coreVersion: String): TaskProvider<DownloadPaperclip> {
        val downloadPaperVersions = tasks.register<Download>("downloadPaperVersions") {
            group = TASKS_GROUP_BUKKIT
            description = "Download file with paperclip versions"

            src(PaperConstants.URL_PAPER_VERSIONS)
            dest(bukkitGradleDir)
            quiet(true)
            onlyIfModified(true)
        }

        return tasks.register<DownloadPaperclip>("downloadPaperclip") {
            paperVersionsFile.set(downloadPaperVersions.map { it.outputFiles.single() })
            version.set(coreVersion)
            dest(bukkitGradleDir)
        }
    }

    private fun registerGenerateRunningScriptTask(serverDir: File): TaskProvider<GenerateRunningScript> {
        return project.tasks.register<GenerateRunningScript>("generateRunningScript") {
            jvmArgs.set(serverConfiguration.buildJvmArgs())
            bukkitArgs.set(serverConfiguration.bukkitArgs)
            scriptDir.set(serverDir)
        }
    }

    private fun registerCopyServerCoreTask(
        buildServerCore: TaskProvider<BuildServerCore>,
        downloadPaperclip: TaskProvider<DownloadPaperclip>,
        serverDir: File
    ): TaskProvider<Copy> {
        return tasks.register<Copy>("copyServerCore") {
            group = TASKS_GROUP_BUKKIT
            description = "Copy server core to server directory"

            val source = if (serverConfiguration.coreType == CoreType.SPIGOT) {
                buildServerCore.map { it.buildToolsFile.get() }
            } else {
                downloadPaperclip.map { it.paperclipFile.get() }
            }

            from(source.map { it.parentFile })
            include(source.get().name) // FIXME: Check if it works properly
            rename { ServerConstants.FILE_CORE }
            into(serverDir)
        }
    }

    private fun registerPrepareServerTask(
        copyServerCore: TaskProvider<Copy>,
        serverDir: File
    ): TaskProvider<PrepareServer> {
        val jarTaskName = if (project.plugins.hasPlugin("com.github.johnrengelman.shadow")) "shadowJar" else "jar"
        val jarTask = tasks.named<Jar>(jarTaskName)
        val copyPlugins = tasks.register<Copy>("copyPlugins") {
            group = TASKS_GROUP_BUKKIT
            description = "Copy plugins to dev server."

            from(jarTask)
            into(project.mkdir(File(serverDir, "plugins")))
            rename { "${pluginMeta.name.get()}.jar" }
        }

        return tasks.register<PrepareServer>("prepareServer") {
            this.serverDir.set(serverDir)
            eula = serverConfiguration.eula
            onlineMode = serverConfiguration.onlineMode
            dependsOn(copyServerCore, copyPlugins)
        }
    }

    private fun registerRunServerTask(
        generateRunningScript: TaskProvider<GenerateRunningScript>,
        prepareServer: TaskProvider<PrepareServer>
    ) {
        tasks.register<RunServer>("runServer") {
            scriptFile.set(generateRunningScript.map { it.scriptFile.get().asFile })
            dependsOn(prepareServer)
        }
    }

    private fun registerBuildIdeRunTask(serverDir: File) {
        tasks.register<CreateIdeaJarRunConfiguration>("buildIdeaRun") {
            configurationName.set("$project.name: Run server")
            beforeRunTask.set("prepareServer")
            configurationsDir.set(project.rootProject.layout.projectDirectory.dir(".idea/runConfigurations"))
            jarPath.set(File(serverDir, ServerConstants.FILE_CORE))
        }
    }
}
