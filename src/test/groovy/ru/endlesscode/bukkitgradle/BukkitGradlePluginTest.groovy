package ru.endlesscode.bukkitgradle

import org.junit.Test

class BukkitGradlePluginTest extends PluginTestBase {

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
        assert project.repositories.findByName("sk89q") != null
    }

    @Test
    void 'when use bukkit extension - and bukkit version not set - should return bukkit dependency with default version'() {
        // When
        String dependency = project.dependencies.bukkit()

        // Then
        assert 'org.bukkit:bukkit:1.16.4-R0.1-SNAPSHOT' == dependency
    }

    @Test
    void 'when use bukkit extension - and bukkit version set - should return bukkit with specified version'() {
        // Given
        project.bukkit.version = "1.7.10"

        // When
        String dependency = project.dependencies.bukkit()

        // Then
        assert 'org.bukkit:bukkit:1.7.10-R0.1-SNAPSHOT' == dependency
    }

    @Test
    void 'when use bukkit extension - should add repo spigot'() {
        // When
        project.dependencies.bukkit()

        // Then
        assert project.repositories.findByName("Spigot") != null
    }

    @Test
    void 'when use spigot extension - should add repo mavenLocal'() {
        // Given
        assert project.repositories.findByName("MavenLocal") == null

        // When
        project.dependencies.spigot()

        // Then
        assert project.repositories.findByName("MavenLocal") != null
    }
}
