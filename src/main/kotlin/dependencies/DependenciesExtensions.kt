@file:Suppress("SpellCheckingInspection", "UnusedReceiverParameter")

package ru.endlesscode.bukkitgradle.dependencies

import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.PAPER_GROUP
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.URL_AIKAR
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.URL_CODEMC
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.URL_DMULLOY2
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.URL_JITPACK
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.URL_MD5
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.URL_PAPERMC
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.URL_PLACEHOLDERAPI
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.URL_SK89Q
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.URL_SPIGOT
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.addRepo
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.withBukkitVersion

public fun RepositoryHandler.spigot(configure: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository =
    addRepo("Spigot", URL_SPIGOT, configure)

public fun RepositoryHandler.sk89q(configure: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository =
    addRepo("sk89q", URL_SK89Q, configure)

public fun RepositoryHandler.papermc(configure: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository =
    addRepo("PaperMC", URL_PAPERMC, configure)

public fun RepositoryHandler.dmulloy2(configure: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository =
    addRepo("dmulloy2", URL_DMULLOY2, configure)

public fun RepositoryHandler.md5(configure: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository =
    addRepo("md5", URL_MD5, configure)

public fun RepositoryHandler.jitpack(configure: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository =
    addRepo("jitpack", URL_JITPACK, configure)

public fun RepositoryHandler.placeholderApi(configure: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository =
    addRepo("PlaceholderAPI", URL_PLACEHOLDERAPI, configure)

public fun RepositoryHandler.aikar(configure: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository =
    addRepo("aikar", URL_AIKAR, configure)

public fun RepositoryHandler.codemc(configure: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository =
    addRepo("codemc", URL_CODEMC, configure)

public val DependencyHandler.spigot: String
    get() = withBukkitVersion("org.spigotmc", "spigot")

public val DependencyHandler.spigotApi: String
    get() = withBukkitVersion("org.spigotmc", "spigot-api")

public val DependencyHandler.bukkitApi: String
    get() = withBukkitVersion("org.bukkit", "bukkit")

public val DependencyHandler.paperApi: String
    get() = withBukkitVersion(PAPER_GROUP, "paper-api")
