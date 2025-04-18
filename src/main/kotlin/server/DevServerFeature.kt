package ru.endlesscode.bukkitgradle.server

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.*
import ru.endlesscode.bukkitgradle.Bukkit
import ru.endlesscode.bukkitgradle.extensions.java
import ru.endlesscode.bukkitgradle.plugin.util.resolveMinimalJavaVersion
import ru.endlesscode.bukkitgradle.server.extension.ServerConfiguration
import ru.endlesscode.bukkitgradle.server.task.CreateIdeaGradleRunConfiguration
import ru.endlesscode.bukkitgradle.server.task.PrepareServer
import xyz.jpenilla.runpaper.RunPaperPlugin
import xyz.jpenilla.runpaper.task.RunServer

internal fun Project.configureDevServerFeature(bukkit: Bukkit) {
    apply<RunPaperPlugin>()
    val configuredServerDir = resolveConfiguredServerDir()

    // Preconfigure the RunServer task
    val serverConfiguration = bukkit.server
    val serverVersion = provider<String> { serverConfiguration.version }.orElse(bukkit.apiVersion)
    val runServer = tasks.named<RunServer>("runServer") {
        version.convention(serverVersion)
        if (configuredServerDir != null) runDirectory.convention(configuredServerDir)
        jvmArgs(serverConfiguration.buildJvmArgs())
        args(serverConfiguration.bukkitArgs)

        defaultCharacterEncoding = serverConfiguration.encoding
        debugOptions {
            enabled.convention(serverConfiguration.debug)
            suspend.convention(false)
        }
    }

    // RunPaperPlugin uses afterEvaluate under the hood, so we have to use afterEvaluate
    // to set our conventions after their ones
    afterEvaluate { configureDefaultJvmForServer() }

    val prepareServer = registerPrepareServerTask(serverConfiguration, runServer)
    runServer.configure { dependsOn(prepareServer) }

    registerBuildIdeRunTask(runServer)
}

private fun Project.configureDefaultJvmForServer() {
    val toolchains = project.extensions.findByType<JavaToolchainService>() ?: return
    val spec = java.toolchain

    tasks.withType<RunServer>().configureEach {
        javaLauncher.convention(
            toolchains.launcherFor {
                languageVersion.convention(version.map(::resolveMinimalJavaVersion))
                implementation.convention(spec.implementation)
                vendor.convention(spec.vendor)
            }
        )
    }
}

private fun Project.resolveConfiguredServerDir(): Provider<Directory>? {
    val deprecated = DeprecatedServerProperties(rootDir, providers)

    val serverDirProperty = providers.gradleProperty("bukkitgradle.server.dir")
        .orElse(provider { deprecated.devServerDir?.absolutePath })
        .orNull ?: return null
    return layout.dir(provider { file(serverDirProperty) })
}

private fun Project.registerPrepareServerTask(
    serverConfiguration: ServerConfiguration,
    runServer: Provider<RunServer>,
): TaskProvider<PrepareServer> {
    return tasks.register<PrepareServer>("prepareServer") {
        serverDir.set(runServer.map { it.runDirectory.get() })
        onlineMode = serverConfiguration.onlineMode
    }
}

private fun Project.registerBuildIdeRunTask(runServer: TaskProvider<RunServer>) {
    tasks.register<CreateIdeaGradleRunConfiguration>("buildIdeaRun") {
        configurationName.set("Run Server [${project.name}]")
        taskNames.add(runServer.name)
    }
}
