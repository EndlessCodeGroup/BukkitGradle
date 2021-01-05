package ru.endlesscode.bukkitgradle

import org.junit.Test
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta
import ru.endlesscode.bukkitgradle.server.ServerConstants

class BukkitExtensionTest extends PluginTestBase {

    @Test
    void testDefaultVersionMustBeFallback() throws Exception {
        assert ServerConstants.DEFAULT_VERSION == project.bukkit.version
    }

    @Test
    void testChangedVersionMustBeRight() throws Exception {
        project.with {
            bukkit.version = '1.7.10'
            assert '1.7.10-R0.1-SNAPSHOT' == "$bukkit.fullVersion".toString()
        }
    }

    @Test
    void testDefaultMetaMustInheritMeta() throws Exception {
        project.with {
            PluginMeta meta = bukkit.meta
            assert name == meta.name.get()
            assert description == meta.description.get()
            assert version == meta.version.get()
            assert ext.url == meta.url.get()
            assert "com.example.plugin.TestProject" == meta.main.get()
            assert [] == meta.authors.get()
        }
    }

    @Test
    void testCustomMetaMustBeRight() throws Exception {
        this.initBukkitMeta()

        PluginMeta meta = this.project.bukkit.meta
        assert "TestPlugin" == meta.name.get()
        assert "Test plugin description" == meta.description.get()
        assert "0.1" == meta.version.get()
        assert "com.example.plugin.Plugin" == meta.main.get()
        assert "http://www.example.com/" == meta.url.get()
        assert ['OsipXD', 'Contributors'] == meta.authors.get()
    }
}
