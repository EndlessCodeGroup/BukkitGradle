package ru.endlesscode.bukkitgradle.meta

import org.gradle.api.GradleException
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta

import java.nio.file.Path

class MetaFileTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder()

    private Path target
    private PluginMeta meta

    // SUT
    private MetaFile metaFile

    @Before
    void setUp() {
        target = createDefaultMetaFile()
        meta = new PluginMeta()
        metaFile = new MetaFile(meta, target)
    }

    @Test
    void 'when initialized - should keep only unsupported lines'() {
        // When
        def lines = target.readLines()

        // Then
        assert ["depend: [Vault, ProtocolLib]", "command:", "  example"] == lines
    }

    @Test
    void 'when write meta to file - and meta configured - should write all lines'() {
        // Given
        configureMeta()

        // When
        metaFile.writeTo(target)

        // Then
        List<String> expected = [
                "name: TestPlugin",
                "description: Test plugin description",
                "main: com.example.plugin.Plugin",
                "version: 0.1",
                "website: http://www.example.com/",
                "authors: [OsipXD, Contributors]",
                "depend: [Vault, ProtocolLib]",
                "command:",
                "  example"
        ]
        assert expected == target.readLines()
    }

    @Test(expected = GradleException)
    void 'when write meta to file - and required field missing - should throw exception'() {
        // Given
        meta.main = null

        // When
        metaFile.writeTo(target)
    }

    private Path createDefaultMetaFile() {
        Path metaFile = tempFolder.newFile(MetaFile.NAME).toPath()
        metaFile << $/
            name: TestPlugin
            description: Test plugin description
            version: 0.1
            
            main: com.example.plugin.Plugin
            author: OsipXD
            website: www.example.com
            
            depend: [Vault, ProtocolLib]
            command:
              example
            /$.stripIndent()
    }

    private void configureMeta(
            String name = "TestPlugin",
            String description = "Test plugin description",
            String version = "0.1",
            String main = "com.example.plugin.Plugin",
            List<String> authors = ["OsipXD", "Contributors"],
            String url = "http://www.example.com/"
    ) {
        meta.name = name
        meta.description = description
        meta.version = version
        meta.main = main
        meta.authors = authors
        meta.url = url
    }
}
