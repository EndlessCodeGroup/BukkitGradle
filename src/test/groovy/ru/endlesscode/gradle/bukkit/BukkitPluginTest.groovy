package ru.endlesscode.gradle.bukkit

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*

class BukkitPluginTest {
    private Project project

    @Before
    void setUp() throws Exception {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply(BukkitPlugin)
    }

    @Test
    void testPluginAddsRequiredPlugins() throws Exception {
        assertTrue project.pluginManager.hasPlugin('java')
        assertTrue project.pluginManager.hasPlugin('idea')
        assertTrue project.pluginManager.hasPlugin('eclipse')
    }

    @Test
    void testPluginAddsRequiredRepos() throws Exception {
        project.repositories.getByName('spigot')
    }
}