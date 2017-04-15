package ru.endlesscode.gradle.bukkit

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import ru.endlesscode.gradle.bukkit.meta.PluginMeta

import static org.junit.Assert.*

class BukkitPluginExtensionTest {
    private Project project

    @Before
    void setUp() throws Exception {
        this.project = ProjectBuilder.builder().build()

        project.with {
            pluginManager.apply BukkitPlugin

            description = "Test Description"
            version = "1.0-alpha"

            ext {
                url = "http://www.example.com/"
            }
        }
    }

    @Test
    void testDefaultVersionMustBeLatest() throws Exception {
        assertEquals "+", project.bukkit.version
    }

    @Test
    void testChangedVersionMustBeRight() throws Exception {
        project.with {
            bukkit.version = "1.7.10"
            assertTrue "1.7.10-R0.1-SNAPSHOT" == "$bukkit.version"
        }
    }

    @Test
    void testDefaultMetaMustInheritMeta() throws Exception {
        project.with {
            PluginMeta meta = bukkit.meta
            assertEquals(name, meta.name)
            assertEquals(description, meta.description)
            assertEquals(version, meta.version)
            assertEquals(ext.url, meta.url)
            assertNull(meta.authors)
            assertNull(meta.main)
        }
    }

    @Test
    void testCustomMetaMustBeRight() throws Exception {
        project.with {
            bukkit.meta {
                name = "TestPlugin"
                description = "My test plugin"
                version = "0.1"
                main = "ru.endlesscode.bukkit.Plugin"
                url = "https://endlesscode.ru/"
                authors = ["OsipXD", "Contributors"]
            }

            PluginMeta meta = bukkit.meta
            assertEquals("TestPlugin", meta.name)
            assertEquals("My test plugin", meta.description)
            assertEquals("0.1", meta.version)
            assertEquals("ru.endlesscode.bukkit.Plugin", meta.main)
            assertEquals("https://endlesscode.ru/", meta.url)
            assertEquals("[OsipXD, Contributors]", meta.authors)
        }
    }
}