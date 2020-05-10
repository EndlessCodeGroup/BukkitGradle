package ru.endlesscode.bukkitgradle

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler

class Dependencies {

    private static Project project
    private static RepositoryHandler repoHandler
    private static DependencyHandler depHandler

    private Dependencies() {}

    static configureProject(Project project) {
        this.project = project
        repoHandler = project.repositories
        depHandler = project.dependencies
        addExtensions()
    }

    private static addExtensions() {
        repoHandler.ext {
            spigot = {
                addRepo('Spigot', 'https://hub.spigotmc.org/nexus/content/repositories/snapshots/')
            }
            sk89q = {
                addRepo('sk89q', 'https://maven.sk89q.com/repo/')
            }
            papermc = {
                addRepo('PaperMC', 'https://papermc.io/repo/repository/maven-public/')
            }
            dmulloy2 = {
                addRepo('dmulloy2', 'https://repo.dmulloy2.net/nexus/repository/public/')
            }
            md5 = {
                addRepo('md5', 'https://repo.md-5.net/content/groups/public/')
            }
            vault = {
                addRepo('Vault', 'http://nexus.hc.to/content/repositories/pub_releases/')
            }
            placeholderApi = {
                addRepo('PlaceholderAPI', 'https://repo.extendedclip.com/content/repositories/placeholderapi/')
            }
            aikar = {
                addRepo('aikar', 'https://repo.aikar.co/content/groups/aikar/')
            }
        }

        depHandler.ext {
            spigot = { api('org.spigotmc', 'spigot') }
            spigotApi = { api('org.spigotmc', 'spigot-api', 'spigot') }
            bukkit = { api('org.bukkit', 'bukkit', 'spigot') }
            craftbukkit = { api('org.bukkit', 'craftbukkit') }
            paperApi = { api('com.destroystokyo.paper', 'paper-api', 'papermc') }
        }
    }

    private static addRepo(repoName, repoUrl) {
        repoHandler.maven {
            name = repoName
            url = repoUrl
        }
    }

    private static Dependency api(String groupId, String artifactId, String... requiredRepos) {
        String version = project.bukkit.version
        return dep(groupId, artifactId, version, requiredRepos)
    }

    private static Dependency dep(String groupId, String artifactId, String version, String... requiredRepos) {
        for (repo in requiredRepos) {
            repoHandler."$repo"()
        }

        return depHandler.create("$groupId:$artifactId:$version")
    }
}
