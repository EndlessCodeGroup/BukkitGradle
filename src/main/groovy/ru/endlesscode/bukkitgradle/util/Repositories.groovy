package ru.endlesscode.bukkitgradle.util

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler

class Repositories {
    private static Project project
    private static RepositoryHandler handler

    private Repositories() {}

    static configureProject(Project project) {
        this.project = project
        handler = project.repositories
        addExtensions()
    }

    private static addExtensions() {
        handler.ext {
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
        }
    }

    private static addRepo(repoName, repoUrl) {
        handler.maven {
            name = repoName
            url = repoUrl
        }
    }
}
