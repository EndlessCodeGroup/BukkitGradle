package ru.endlesscode.bukkitgradle.dependencies

import groovy.lang.Closure
import org.codehaus.groovy.runtime.InvokerHelper
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.kotlin.dsl.KotlinClosure0
import org.gradle.kotlin.dsl.closureOf
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.maven
import ru.endlesscode.bukkitgradle.bukkit

public object Dependencies {

    internal const val URL_SPIGOT = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/"
    internal const val URL_SK89Q = "https://maven.sk89q.com/repo/"
    internal const val URL_PAPERMC = "https://papermc.io/repo/repository/maven-public/"
    internal const val URL_DMULLOY2 = "https://repo.dmulloy2.net/nexus/repository/public/"
    internal const val URL_MD5 = "https://repo.md-5.net/content/groups/public/"
    internal const val URL_VAULT = "http://nexus.hc.to/content/repositories/pub_releases/"
    internal const val URL_PLACEHOLDERAPI = "https://repo.extendedclip.com/content/repositories/placeholderapi/"
    internal const val URL_AIKAR = "https://repo.aikar.co/content/groups/aikar/"

    private lateinit var project: Project
    private lateinit var repoHandler: RepositoryHandler
    private lateinit var depHandler: DependencyHandler

    private val RepositoryHandler.extra: ExtraPropertiesExtension
        get() = InvokerHelper.getProperty(this, "ext") as ExtraPropertiesExtension

    @JvmStatic
    public fun configureProject(project: Project) {
        Dependencies.project = project
        repoHandler = project.repositories
        depHandler = project.dependencies
        addGroovyExtensions()
    }

    private fun addGroovyExtensions() {
        val repoExtra = repoHandler.extra
        repoExtra["spigot"] = closureOf<Any?> { repoHandler.addRepo("Spigot", URL_SPIGOT) }
        repoExtra["sk89q"] = closureOf<Any?> { repoHandler.addRepo("sk89q", URL_SK89Q) }
        repoExtra["papermc"] = closureOf<Any?> { repoHandler.addRepo("PaperMC", URL_PAPERMC) }
        repoExtra["dmulloy2"] = closureOf<Any?> { repoHandler.addRepo("dmulloy2", URL_DMULLOY2) }
        repoExtra["md5"] = closureOf<Any?> { repoHandler.addRepo("md5", URL_MD5) }
        repoExtra["vault"] = closureOf<Any?> { repoHandler.addRepo("Vault", URL_VAULT) }
        repoExtra["placeholderApi"] = closureOf<Any?> { repoHandler.addRepo("PlaceholderAPI", URL_PLACEHOLDERAPI) }
        repoExtra["aikar"] = closureOf<Any?> { repoHandler.addRepo("aikar", URL_AIKAR) }

        val depExtra = depHandler.extra
        depExtra["spigot"] = depClosureOf { depHandler.api("org.spigotmc", "spigot", "mavenLocal") }
        depExtra["spigotApi"] = depClosureOf { depHandler.api("org.spigotmc", "spigot-api", "spigot") }
        depExtra["bukkit"] = depClosureOf { depHandler.api("org.bukkit", "bukkit", "spigot") }
        depExtra["paperApi"] = depClosureOf { depHandler.api("com.destroystokyo.paper", "paper-api", "papermc") }
    }

    internal fun RepositoryHandler.addRepo(
        repoName: String,
        repoUrl: String,
        configure: MavenArtifactRepository.() -> Unit = {}
    ) {
        maven(repoUrl) {
            name = repoName
            configure()
        }
    }

    @Suppress("unused") // Receiver required for scope
    internal fun DependencyHandler.api(groupId: String, artifactId: String, vararg requiredRepos: String): String {
        val version = "${project.bukkit.apiVersion}-R0.1-SNAPSHOT"
        return dep(groupId, artifactId, version, *requiredRepos)
    }

    private fun dep(groupId: String, artifactId: String, version: String, vararg requiredRepos: String): String {
        for (repo in requiredRepos) {
            if (repo == "mavenLocal") {
                repoHandler.mavenLocal()
            } else {
                (repoHandler.extra[repo] as Closure<*>).call(repoHandler)
            }
        }

        return "$groupId:$artifactId:$version"
    }

    private fun depClosureOf(body: () -> String) = KotlinClosure0(body)
}