package ru.endlesscode.bukkitgradle

import org.gradle.api.artifacts.Dependency
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class BukkitGradlePluginTest extends TestBase {
    @Test
    void 'when initialized - should add required plugins'() {
        // When
        def hasJavaPlugin = project.pluginManager.hasPlugin("java")

        // Then
        assert hasJavaPlugin
    }

    @Test
    void 'when use custom repos extension - should add repos'() {
        // When
        project.repositories.sk89q()

        // Then
        project.repositories.getByName("sk89q")
    }

    @Test
    void 'when use bukkit extension - and bukkit version not set - should return bukkit dependency without version'() {
        // When
        Dependency dependency = project.dependencies.bukkit()

        // Then
        assert 'org.bukkit' == dependency.group
        assert 'bukkit' == dependency.name
        assert '+' == dependency.version
    }

    @Test
    void 'when use bukkit extension - and bukkit version set - should return bukkit with specified version'() {
        // Given
        project.bukkit.version = "1.7.10"

        // When
        Dependency dependency = project.dependencies.bukkit()

        // Then
        assert 'org.bukkit' == dependency.group
        assert 'bukkit' == dependency.name
        assert '1.7.10-R0.1-SNAPSHOT' == dependency.version
    }

    @Test
    void 'when use bukkit extension - should add repo spigot'() {
        // When
        project.dependencies.bukkit()

        // Then
        project.repositories.getByName("Spigot")
    }

    @Test
    void 'when use spigot extension - should add repo mavenLocal'() {
        project.repositories.getByName("MavenLocal")
        // When
        project.dependencies.spigot()

        // Then
        project.repositories.getByName("MavenLocal")
    }
}
