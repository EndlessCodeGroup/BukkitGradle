package ru.endlesscode.bukkitgradle

import org.junit.Test
import ru.endlesscode.bukkitgradle.meta.PluginMeta

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

class BukkitTest extends TestBase {
    @Test
    void testDefaultVersionMustBeLatest() throws Exception {
        assertEquals "+", project.bukkit.version
    }

    @Test
    void testChangedVersionMustBeRight() throws Exception {
        project.with {
            bukkit.version = '1.7.10'
            assertEquals('1.7.10-R0.1-SNAPSHOT', "$bukkit.version".toString())
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
            assertEquals("com.example.plugin.testproject.TestProject", meta.main)
            assertNull(meta.authors)
        }
    }

    @Test
    void testCustomMetaMustBeRight() throws Exception {
        this.initBukkitMeta()

        PluginMeta meta = this.project.bukkit.meta
        assertEquals("TestPlugin", meta.name)
        assertEquals("Test plugin description", meta.description)
        assertEquals("0.1", meta.version)
        assertEquals("com.example.plugin.Plugin", meta.main)
        assertEquals("http://www.example.com/", meta.url)
        assertEquals("[OsipXD, Contributors]", meta.authors)
    }
}
