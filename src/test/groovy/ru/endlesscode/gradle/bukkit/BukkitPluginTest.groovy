package ru.endlesscode.gradle.bukkit

import org.gradle.api.artifacts.Dependency
import org.junit.Test

import static org.junit.Assert.assertTrue

class BukkitPluginTest extends TestBase {
    @Test
    void testPluginAddsRequiredPlugins() throws Exception {
        assertTrue project.pluginManager.hasPlugin("java")
        assertTrue project.pluginManager.hasPlugin("idea")
        assertTrue project.pluginManager.hasPlugin("eclipse")
    }

    @Test
    void testPluginAddsRequiredRepos() throws Exception {
        project.repositories.getByName("spigot")
    }

    @Test
    void testPluginAddsLatestBukkitVersion() throws Exception {
        BukkitPlugin.addBukkitApi(project)
        def dependencies = getDependencies()
        assertTrue dependencies.contains("org.bukkit:bukkit:+")
    }

    @Test
    void testPluginAddsCustomBukkit() throws Exception {
        project.bukkit.version = "1.7.10"
        BukkitPlugin.addBukkitApi(project)

        def dependencies = getDependencies()
        assertTrue dependencies.contains("org.bukkit:bukkit:1.7.10-R0.1-SNAPSHOT")
    }

    private String[] getDependencies() {
        def dependencies = []
        for (Dependency dependency : project.configurations.compile.getDependencies()) {
            dependencies << "$dependency.group:$dependency.name:$dependency.version"
        }

        return dependencies
    }
}