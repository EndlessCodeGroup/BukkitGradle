package ru.endlesscode.gradle.bukkit.meta

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import ru.endlesscode.gradle.bukkit.BukkitPlugin

import static org.junit.Assert.assertTrue

class GenerateMetaTest {
    private Project project

    @Before
    void setUp() throws Exception {
        this.project = ProjectBuilder.builder().build()
        project.with {
            pluginManager.apply BukkitPlugin

            description = "Test Description"
            version = "1.0"

            ext {
                url = "http://www.example.com/"
            }

            bukkit.meta {
                main = BukkitPlugin
            }
        }
    }

    @Test
    void testCanAddTaskToProject() throws Exception {
        def task = project.tasks.getByName('generatePluginMeta')
        assertTrue(task instanceof GenerateMeta)
    }
}