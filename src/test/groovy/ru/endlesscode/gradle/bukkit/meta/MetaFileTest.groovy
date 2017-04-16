package ru.endlesscode.gradle.bukkit.meta

import org.gradle.api.GradleException
import org.junit.Before
import org.junit.Test
import ru.endlesscode.gradle.bukkit.TestBase

import java.nio.file.Path

import static org.junit.Assert.assertEquals

class MetaFileTest extends TestBase {
    private MetaFile metaFile
    private Path target

    @Before
    void setUp() throws Exception {
        super.setUp()

        this.target = this.createDefaultMetaFile()
        this.metaFile = new MetaFile(this.project, this.target)
    }

    @Test
    void testRemovingMetaLines() throws Exception {
        assertEquals(["depend: [Vault, ProtocolLib]"], this.target.readLines())
    }

    @Test
    void testGeneratingMeta() throws Exception {
        this.initBukkitMeta()

        List<String> expected = [
                "name: TestPlugin",
                "description: Test plugin description",
                "main: com.example.plugin.Plugin",
                "version: 0.1",
                "website: http://www.example.com/",
                "authors: [OsipXD, Contributors]",
                "depend: [Vault, ProtocolLib]"
        ]

        this.metaFile.writeTo(target)
        assertEquals(expected, target.readLines())
    }

    @Test(expected = GradleException)
    void testMissingRequiredMetaMustThrowException() throws Exception {
        this.metaFile.writeTo(target)
    }
}