package ru.endlesscode.bukkitgradle

import org.gradle.api.artifacts.Dependency
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class BukkitGradlePluginTest extends TestBase {
    @Test
    void testPluginAddsRequiredPlugins() throws Exception {
        assertTrue project.pluginManager.hasPlugin("java")
        assertTrue project.pluginManager.hasPlugin("idea")
        assertTrue project.pluginManager.hasPlugin("eclipse")
    }

    @Test
    void testPluginAddsRequiredRepos() throws Exception {
        project.repositories {
            sk89q()
        }

        project.repositories.getByName("sk89q-repo")
    }

    @Test
    void testPluginAddsLatestBukkitVersion() throws Exception {

        Dependency dependency = project.dependencies.ext.bukkit()
        assertEquals('org.bukkit', dependency.group)
        assertEquals('bukkit', dependency.name)
        assertEquals('+', dependency.version)
        project.repositories.getByName("spigot-repo")
    }

    @Test
    void testPluginAddsCustomBukkit() throws Exception {
        project.bukkit.version = "1.7.10"

        Dependency dependency = project.dependencies.ext.bukkit()
        assertEquals('org.bukkit', dependency.group)
        assertEquals('bukkit', dependency.name)
        assertEquals('1.7.10-R0.1-SNAPSHOT', dependency.version)
    }

    private String[] getDependencies() {
        def dependencies = []

        for (Dependency dependency : project.configurations.compileOnly.getDependencies()) {
            dependencies << "$dependency.group:$dependency.name:$dependency.version"
        }

        return dependencies
    }
}
