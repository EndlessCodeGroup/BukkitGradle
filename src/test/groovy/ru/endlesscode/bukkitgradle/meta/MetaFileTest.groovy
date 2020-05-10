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

    private Path source
    private Path target
    private PluginMeta meta

    // SUT
    private MetaFile metaFile

    @Before
    void setUp() {
        source = tempFolder.newFile("source-$MetaFile.NAME").toPath()
        target = tempFolder.newFile(MetaFile.NAME).toPath()
        meta = new PluginMeta()
        metaFile = new MetaFile(meta, source)
    }

    @Test
    void 'when write meta to file - should keep only unsupported lines in source file'() {
        // Given
        configureMeta()
        source << $/
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

        // When
        metaFile.writeTo(target)

        // Then
        assert ["depend: [Vault, ProtocolLib]", "command:", "  example"] == source.readLines()
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
                "authors: [OsipXD, Contributors]"
        ]
        assert expected == target.readLines()
    }

    @Test
    void 'when write meta to file - and there are extra fields in source - should write all lines'() {
        // Given
        configureMeta()
        source << $/
            depend: [Vault, ProtocolLib]
            command:
              example
            /$.stripIndent()

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

    private void configureMeta(
            name = "TestPlugin",
            description = "Test plugin description",
            version = "0.1",
            main = "com.example.plugin.Plugin",
            authors = ["OsipXD", "Contributors"],
            url = "http://www.example.com/"
    ) {
        meta.name = name
        meta.description = description
        meta.version = version
        meta.main = main
        meta.authors = authors
        meta.url = url
    }
}
