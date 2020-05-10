package ru.endlesscode.bukkitgradle

import org.junit.Test
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta

class BukkitTest extends PluginTestBase {

    @Test
    void testDefaultVersionMustBeLatest() throws Exception {
        assert "+" == project.bukkit.version
    }

    @Test
    void testChangedVersionMustBeRight() throws Exception {
        project.with {
            bukkit.version = '1.7.10'
            assert '1.7.10-R0.1-SNAPSHOT' == "$bukkit.version".toString()
        }
    }

    @Test
    void testDefaultMetaMustInheritMeta() throws Exception {
        project.with {
            PluginMeta meta = bukkit.meta
            assert name == meta.name
            assert description == meta.description
            assert version == meta.version
            assert ext.url == meta.url
            assert "com.example.plugin.testproject.TestProject" == meta.main
            assert meta.authors == null
        }
    }

    @Test
    void testCustomMetaMustBeRight() throws Exception {
        this.initBukkitMeta()

        PluginMeta meta = this.project.bukkit.meta
        assert "TestPlugin" == meta.name
        assert "Test plugin description" == meta.description
        assert "0.1" == meta.version
        assert "com.example.plugin.Plugin" == meta.main
        assert "http://www.example.com/" == meta.url
        assert "[OsipXD, Contributors]" == meta.authors
    }
}
