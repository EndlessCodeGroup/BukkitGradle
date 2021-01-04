package ru.endlesscode.bukkitgradle.server.legacy

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta
import ru.endlesscode.bukkitgradle.server.ServerConstants
import ru.endlesscode.bukkitgradle.server.extension.RunConfiguration
import ru.endlesscode.bukkitgradle.server.task.CreateIdeaJarRunConfiguration
import ru.endlesscode.bukkitgradle.server.task.GenerateRunningScript
import ru.endlesscode.bukkitgradle.server.task.PrepareServer
import ru.endlesscode.bukkitgradle.server.task.RunServer

class LegacyDevServerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        ServerCore serverCore = new ServerCore(project)
        def configuration = project.bukkit.run as RunConfiguration
        def generateRunningScript = project.tasks.register('generateRunningScript', GenerateRunningScript) {
            jvmArgs.set(configuration.buildJvmArgs())
            bukkitArgs.set(configuration.bukkitArgs)
            scriptDir.set(serverCore.serverDir)
        }

        def prepareServer = configurePrepareServerTask(project, serverCore)
        project.tasks.register('runServer', RunServer) {
            scriptFile.set(generateRunningScript.map { it.scriptFile.get().asFile })
            dependsOn(prepareServer)
        }

        project.tasks.register('buildIdeaRun', CreateIdeaJarRunConfiguration) {
            configurationName.set("$project.name: Run server")
            beforeRunTask.set('prepareServer')
            configurationsDir.set(project.rootProject.layout.projectDirectory.dir('.idea/runConfigurations'))
            jarPath.set(new File(serverCore.serverDir, ServerConstants.FILE_CORE))
        }
    }

    private static TaskProvider<PrepareServer> configurePrepareServerTask(Project project, ServerCore serverCore) {
        def jarTaskName = project.plugins.hasPlugin("com.github.johnrengelman.shadow") ? "shadowJar" : "jar"
        def jarTask = project.tasks.named(jarTaskName, Jar) as TaskProvider<Jar>
        def meta = project.bukkit.meta as PluginMeta
        def copyPlugins = project.tasks.register("copyPlugins", Copy) {
            from(jarTask)
            into(project.mkdir(new File(serverCore.serverDir, "plugins")))
            rename { "${meta.name.get()}.jar" }
        }

        def run = project.bukkit.run as RunConfiguration
        return project.tasks.register('prepareServer', PrepareServer) {
            serverDir.set(serverCore.serverDir)
            eula = run.eula
            onlineMode = run.onlineMode
            dependsOn('copyServerCore', copyPlugins)
        }
    }
}
