package ru.endlesscode.bukkitgradle.meta

import groovy.transform.NamedVariant
import org.gradle.api.GradleException
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta
import ru.endlesscode.bukkitgradle.util.CharsetUtils

import java.lang.reflect.Field
import java.nio.charset.Charset
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

        configureMeta()
    }

    @Test
    void 'when write meta to file - should keep only unsupported lines in source file'() {
        // Given
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
    void 'when write meta to file - and all meta configured - should write all lines'() {
        // Given
        configureMeta(
                description: "Test plugin description",
                authors: ["OsipXD", "Contributors"],
                url: "http://www.example.com/"
        )

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
                "main: com.example.plugin.Plugin",
                "version: 0.1",
                "depend: [Vault, ProtocolLib]",
                "command:",
                "  example"
        ]
        assert expected == target.readLines()
    }

    @Test
    void 'when write meta to file - and there are exotic chars in source - should read it correctly'() {
        // Given
        source.append($/
            commands:
              퀘스트:
                description: 퀘스트 명령어 입니다.
            /$.stripIndent())

        // When
        CharsetUtils.setDefaultCharset('CP866')
        metaFile.writeTo(target)
        CharsetUtils.setDefaultCharset('UTF-8')

        // Then
        List<String> expected = [
                "name: TestPlugin",
                "main: com.example.plugin.Plugin",
                "version: 0.1",
                "commands:",
                "  퀘스트:",
                "    description: 퀘스트 명령어 입니다."
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

    @NamedVariant
    private void configureMeta(
            name = null,
            description = null,
            version = null,
            main = null,
            authors = null,
            url = null
    ) {
        meta.name = name ?: "TestPlugin"
        meta.description = description
        meta.version = version ?: "0.1"
        meta.main = main ?: "com.example.plugin.Plugin"
        meta.authors = authors
        meta.url = url
    }
}
