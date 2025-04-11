package ru.endlesscode.bukkitgradle.server

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import ru.endlesscode.bukkitgradle.Bukkit
import ru.endlesscode.bukkitgradle.bukkit
import ru.endlesscode.bukkitgradle.server.extension.ServerConfiguration
import ru.endlesscode.bukkitgradle.server.task.CreateIdeaJarRunConfiguration
import ru.endlesscode.bukkitgradle.server.task.PrepareServer
import xyz.jpenilla.runpaper.task.RunServer
import java.io.File

public class DevServerPlugin : Plugin<Project> {

    private lateinit var project: Project
    private lateinit var bukkit: Bukkit

    private val serverConfiguration: ServerConfiguration
        get() = bukkit.server

    private val tasks: TaskContainer
        get() = project.tasks

    @Override
    override fun apply(target: Project) {
        project = target
        bukkit = project.bukkit

        target.plugins.apply("xyz.jpenilla.run-paper")
        val configuredServerDir = target.resolveConfiguredServerDir()

        // Preconfigure RunServer task
        val serverVersion = project.provider<String> { serverConfiguration.version }.orElse(bukkit.apiVersion)
        val runServer = tasks.named<RunServer>("runServer") {
            version.convention(serverVersion)
            if (configuredServerDir != null) runDirectory.convention(configuredServerDir)
            jvmArgs(serverConfiguration.buildJvmArgs())
            args(serverConfiguration.bukkitArgs)
        }

        val prepareServer = registerPrepareServerTask(runServer)
        runServer.configure { dependsOn(prepareServer) }

        registerBuildIdeRunTask(runServer)
    }

    private fun Project.resolveConfiguredServerDir(): Provider<Directory>? {
        val serverDirProperty = providers.gradleProperty("bukkitgradle.server.dir").orNull ?: return null
        val serverDirFile = provider { File(serverDirProperty).absoluteFile }
        return layout.dir(serverDirFile)
    }

    private fun registerPrepareServerTask(
        runServer: Provider<RunServer>,
    ): TaskProvider<PrepareServer> {
        return tasks.register<PrepareServer>("prepareServer") {
            this.serverDir.set(runServer.map { it.runDirectory.get() })
            eula = serverConfiguration.eula
            onlineMode = serverConfiguration.onlineMode
        }
    }

    private fun registerBuildIdeRunTask(runServer: Provider<RunServer>) {
        tasks.register<CreateIdeaJarRunConfiguration>("buildIdeaRun") {
            configurationName.set("${project.name}: Run server")
            beforeRunTask.set("prepareServer")
            vmParameters.set(runServer.map { it.jvmArgs })
            programParameters.set(runServer.map { it.args })
            configurationsDir.set(project.rootProject.layout.projectDirectory.dir(".idea/runConfigurations"))
            jarPath.set(runServer.map { it.classpath.singleFile })
            workingDirectory.set(runServer.map { it.runDirectory.get().asFile })
        }
    }
}
