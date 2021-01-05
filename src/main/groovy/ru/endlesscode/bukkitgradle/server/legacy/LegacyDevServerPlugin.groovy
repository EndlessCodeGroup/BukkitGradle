package ru.endlesscode.bukkitgradle.server.legacy

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import ru.endlesscode.bukkitgradle.Bukkit
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta
import ru.endlesscode.bukkitgradle.server.ServerConstants
import ru.endlesscode.bukkitgradle.server.extension.RunConfiguration
import ru.endlesscode.bukkitgradle.server.task.CreateIdeaJarRunConfiguration
import ru.endlesscode.bukkitgradle.server.task.GenerateRunningScript
import ru.endlesscode.bukkitgradle.server.task.PrepareServer
import ru.endlesscode.bukkitgradle.server.task.RunServer

class LegacyDevServerPlugin implements Plugin<Project> {

    private Project project
    private Bukkit bukkit

    @Override
    void apply(Project project) {
        this.project = project
        bukkit = project.bukkit as Bukkit

        ServerCore serverCore = new ServerCore(project)
        def generateRunningScript = project.tasks.register('generateRunningScript', GenerateRunningScript) {
            jvmArgs.set(runConfiguration.buildJvmArgs())
            bukkitArgs.set(runConfiguration.bukkitArgs)
            scriptDir.set(serverCore.serverDir)
        }

        def prepareServer = configurePrepareServerTask(serverCore)
        tasks.register('runServer', RunServer) {
            scriptFile.set(generateRunningScript.map { it.scriptFile.get().asFile })
            dependsOn(prepareServer)
        }

        tasks.register('buildIdeaRun', CreateIdeaJarRunConfiguration) {
            configurationName.set("$project.name: Run server")
            beforeRunTask.set('prepareServer')
            configurationsDir.set(project.rootProject.layout.projectDirectory.dir('.idea/runConfigurations'))
            jarPath.set(new File(serverCore.serverDir, ServerConstants.FILE_CORE))
        }
    }

    private TaskProvider<PrepareServer> configurePrepareServerTask(ServerCore serverCore) {
        def jarTaskName = project.plugins.hasPlugin("com.github.johnrengelman.shadow") ? "shadowJar" : "jar"
        def jarTask = tasks.named(jarTaskName, Jar) as TaskProvider<Jar>
        def copyPlugins = tasks.register("copyPlugins", Copy) {
            from(jarTask)
            into(project.mkdir(new File(serverCore.serverDir, "plugins")))
            rename { "${pluginMeta.name.get()}.jar" }
        }

        return tasks.register('prepareServer', PrepareServer) {
            serverDir.set(serverCore.serverDir)
            eula = runConfiguration.eula
            onlineMode = runConfiguration.onlineMode
            dependsOn('copyServerCore', copyPlugins)
        }
    }

    private RunConfiguration getRunConfiguration() {
        return bukkit.run
    }

    private PluginMeta getPluginMeta() {
        return bukkit.meta
    }

    private TaskContainer getTasks() {
        return project.tasks
    }
}
