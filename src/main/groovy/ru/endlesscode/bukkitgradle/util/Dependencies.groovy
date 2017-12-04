package ru.endlesscode.bukkitgradle.util

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler

class Dependencies {

    private static Project project
    private static DependencyHandler handler

    private Dependencies() {}

    static configureProject(Project project) {
        this.project = project
        handler = project.dependencies
        addExtensions()
    }

    private static addExtensions() {
        handler.ext {
            spigot = { api('org.spigotmc', 'spigot') }
            spigotApi = { api('org.spigotmc', 'spigot-api') }
            bukkit = { api('org.bukkit', 'bukkit') }
            craftbukkit = { api('org.bukkit', 'craftbukkit') }
        }
    }

    private static Dependency api(String groupId, String artifactId) {
        def version = project.bukkit.version
        return handler.create("$groupId:$artifactId:$version")
    }
}
