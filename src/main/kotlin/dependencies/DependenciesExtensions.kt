package ru.endlesscode.bukkitgradle.dependencies

import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.URL_AIKAR
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.URL_DMULLOY2
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.URL_MD5
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.URL_PAPERMC
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.URL_PLACEHOLDERAPI
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.URL_SK89Q
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.URL_SPIGOT
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.URL_VAULT
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.addRepo
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.api

public fun RepositoryHandler.spigot(configure: MavenArtifactRepository.() -> Unit = {}) {
    addRepo("Spigot", URL_SPIGOT, configure)
}

public fun RepositoryHandler.sk89q(configure: MavenArtifactRepository.() -> Unit = {}) {
    addRepo("sk89q", URL_SK89Q, configure)
}

public fun RepositoryHandler.papermc(configure: MavenArtifactRepository.() -> Unit = {}) {
    addRepo("PaperMC", URL_PAPERMC, configure)
}

public fun RepositoryHandler.dmulloy2(configure: MavenArtifactRepository.() -> Unit = {}) {
    addRepo("dmulloy2", URL_DMULLOY2, configure)
}

public fun RepositoryHandler.md5(configure: MavenArtifactRepository.() -> Unit = {}) {
    addRepo("md5", URL_MD5, configure)
}

public fun RepositoryHandler.vault(configure: MavenArtifactRepository.() -> Unit = {}) {
    addRepo("Vault", URL_VAULT, configure)
}

public fun RepositoryHandler.placeholderApi(configure: MavenArtifactRepository.() -> Unit = {}) {
    addRepo("PlaceholderAPI", URL_PLACEHOLDERAPI, configure)
}

public fun RepositoryHandler.aikar(configure: MavenArtifactRepository.() -> Unit = {}) {
    addRepo("aikar", URL_AIKAR, configure)
}

public val DependencyHandler.spigot: String
    get() = api("org.spigotmc", "spigot", "mavenLocal")

public val DependencyHandler.spigotApi: String
    get() = api("org.spigotmc", "spigot-api", "spigot")

public val DependencyHandler.bukkit: String
    get() = api("org.bukkit", "bukkit", "spigot")

public val DependencyHandler.paperApi: String
    get() = api("com.destroystokyo.paper", "paper-api", "papermc")
