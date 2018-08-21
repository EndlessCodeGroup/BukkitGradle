package ru.endlesscode.bukkitgradle.util

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
                addRepo('spigot-repo', 'https://hub.spigotmc.org/nexus/content/repositories/snapshots/')
            }
            sk89q = {
                addRepo('sk89q-repo', 'http://maven.sk89q.com/repo/')
            }
            destroystokyo = {
                addRepo('destroystokyo-repo', 'https://repo.destroystokyo.com/repository/maven-public/')
            }
            dmulloy2 = {
                addRepo('dmulloy2-repo', 'http://repo.dmulloy2.net/nexus/repository/public/')
            }
            md5 = {
                addRepo('md5-repo', 'http://repo.md-5.net/content/groups/public/')
            }
            vault = {
                addRepo('vault-repo', 'http://nexus.hc.to/content/repositories/pub_releases/')
            }
            placeholderapi = {
                addRepo('placeholderapi-repo', 'http://repo.extendedclip.com/content/repositories/placeholderapi/')
            }
            aikar = {
                addRepo('aikar-repo', 'https://repo.aikar.co/content/groups/aikar/')
            }
        }

        depHandler.ext {
            spigot = { api('org.spigotmc', 'spigot') }
            spigotApi = { api('org.spigotmc', 'spigot-api', 'spigot') }
            bukkit = { api('org.bukkit', 'bukkit', 'spigot') }
            craftbukkit = { api('org.bukkit', 'craftbukkit') }
            paperApi = { api('com.destroystokyo.paper', 'paper-api', 'destroystokyo') }
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
            repoHandler.ext."$repo"()
        }

        return depHandler.create("$groupId:$artifactId:$version")
    }
}
