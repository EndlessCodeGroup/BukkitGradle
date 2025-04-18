package ru.endlesscode.bukkitgradle.dependencies

import groovy.lang.Closure
import org.codehaus.groovy.runtime.InvokerHelper
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.maven
import ru.endlesscode.bukkitgradle.BukkitExtension
import ru.endlesscode.bukkitgradle.plugin.util.MinecraftVersion

private typealias RepositoryClosure = Closure<MavenArtifactRepository>

internal object Dependencies {

    const val URL_SPIGOT = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/"
    const val URL_SK89Q = "https://maven.sk89q.com/repo/"
    const val URL_PAPERMC = "https://repo.papermc.io/repository/maven-public/"
    const val URL_DMULLOY2 = "https://repo.dmulloy2.net/nexus/repository/public/"
    const val URL_MD5 = "https://repo.md-5.net/content/groups/public/"
    const val URL_JITPACK = "https://jitpack.io/"
    const val URL_PLACEHOLDERAPI = "https://repo.extendedclip.com/content/repositories/placeholderapi/"
    const val URL_AIKAR = "https://repo.aikar.co/content/groups/aikar/"
    const val URL_CODEMC = "https://repo.codemc.org/repository/maven-public/"

    const val BUKKIT_VERSION_PLACEHOLDER = "{bukkit.apiVersion}"
    const val BUKKIT_VERSION_SUFFIX = "-R0.1-SNAPSHOT"

    const val PAPER_GROUP = "io.papermc.paper"
    const val PAPER_OLD_GROUP = "com.destroystokyo.paper"

    private val RepositoryHandler.extra: ExtraPropertiesExtension
        get() = InvokerHelper.getProperty(this, "ext") as ExtraPropertiesExtension

    @JvmStatic
    fun Project.configureDependencyExtensions(bukkit: BukkitExtension) {
        repositories.addGroovyExtensions()
        dependencies.addGroovyExtensions()

        configureResolutionStrategy(bukkit)
    }

    private fun RepositoryHandler.addGroovyExtensions() {
        extra["spigot"] = repositoryClosure("Spigot", URL_SPIGOT)
        extra["sk89q"] = repositoryClosure("sk89q", URL_SK89Q)
        extra["papermc"] = repositoryClosure("PaperMC", URL_PAPERMC)
        extra["dmulloy2"] = repositoryClosure("dmulloy2", URL_DMULLOY2)
        extra["md5"] = repositoryClosure("md5", URL_MD5)
        extra["jitpack"] = repositoryClosure("jitpack", URL_JITPACK)
        extra["placeholderapi"] = repositoryClosure("PlaceholderAPI", URL_PLACEHOLDERAPI)
        extra["aikar"] = repositoryClosure("aikar", URL_AIKAR)
        extra["codemc"] = repositoryClosure("codemc", URL_CODEMC)
    }

    private fun DependencyHandler.addGroovyExtensions() {
        extra["spigot"] = withBukkitVersion("org.spigotmc", "spigot")
        extra["spigotApi"] = withBukkitVersion("org.spigotmc", "spigot-api")
        extra["bukkitApi"] = withBukkitVersion("org.bukkit", "bukkit")
        extra["paperApi"] = withBukkitVersion(PAPER_GROUP, "paper-api")
    }

    private fun RepositoryHandler.repositoryClosure(name: String, url: String): RepositoryClosure =
        object : RepositoryClosure(this, this) {
            @Suppress("unused") // to be called dynamically by Groovy
            fun doCall() = (delegate as RepositoryHandler).addRepo(name, url)
        }

    fun RepositoryHandler.addRepo(
        repoName: String,
        repoUrl: String,
        configure: MavenArtifactRepository.() -> Unit = {}
    ): MavenArtifactRepository {
        return maven(repoUrl) {
            name = repoName
            configure()
        }
    }

    fun withBukkitVersion(groupId: String, artifactId: String): String {
        return "$groupId:$artifactId:$BUKKIT_VERSION_PLACEHOLDER"
    }

    private fun Project.configureResolutionStrategy(bukkit: BukkitExtension) {
        val bukkitVersion = bukkit.finalApiVersion
            .map { "$it$BUKKIT_VERSION_SUFFIX" }

        configurations.configureEach {
            resolutionStrategy.eachDependency {
                val version = if (requested.version == BUKKIT_VERSION_PLACEHOLDER) {
                    bukkitVersion.get()
                } else {
                    requested.version
                }

                val shouldUseOldPaperGroup by lazy {
                    version != null && MinecraftVersion.parse(version) < MinecraftVersion.V1_17_0
                }
                val group = when (requested.group) {
                    PAPER_GROUP -> if (shouldUseOldPaperGroup) PAPER_OLD_GROUP else requested.group
                    PAPER_OLD_GROUP -> if (!shouldUseOldPaperGroup) PAPER_GROUP else requested.group
                    else -> requested.group
                }

                if (requested.group != group) {
                    useTarget("$group:${requested.name}:$version")
                    because("Fix paper group as it was changed in 1.17")
                } else if (requested.version != version && version != null) {
                    useVersion(version)
                    because("Substitute bukkit version")
                }
            }
        }
    }
}
