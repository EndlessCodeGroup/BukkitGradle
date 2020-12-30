package ru.endlesscode.bukkitgradle.meta

import groovy.transform.NamedVariant
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta
import ru.endlesscode.bukkitgradle.util.CharsetUtils
import spock.lang.Specification

class MetaFileSpec extends Specification {

    @Rule
    TemporaryFolder tempFolder = new TemporaryFolder()

    private File source
    private File target
    private PluginMeta meta

    // SUT
    private MetaFile metaFile

    def setup() {
        source = tempFolder.newFile("source-$MetaFile.NAME")
        target = tempFolder.newFile(MetaFile.NAME)
        meta = new PluginMeta()
        metaFile = new MetaFile(meta, source)

        configureMeta()
    }

    void 'when write meta to file - should keep only unsupported lines in source file'() {
        given:
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

        when:
        metaFile.writeTo(target)

        then:
        source.readLines() == ["depend: [Vault, ProtocolLib]", "command:", "  example"]
    }

    void 'when write meta to file - and all meta configured - should write all lines'() {
        given:
        configureMeta(
                description: "Test plugin description",
                authors: ["OsipXD", "Contributors"],
                url: "http://www.example.com/"
        )

        when:
        metaFile.writeTo(target)

        then:
        target.readLines() == [
                "name: TestPlugin",
                "description: Test plugin description",
                "main: com.example.plugin.Plugin",
                "version: 0.1",
                "website: http://www.example.com/",
                "authors: [OsipXD, Contributors]"
        ]
    }

    void 'when write meta to file - and there are extra fields in source - should write all lines'() {
        given:
        source << $/
            depend: [Vault, ProtocolLib]
            command:
              example
        /$.stripIndent()

        when:
        metaFile.writeTo(target)

        then:
        target.readLines() == [
                "name: TestPlugin",
                "main: com.example.plugin.Plugin",
                "version: 0.1",
                "depend: [Vault, ProtocolLib]",
                "command:",
                "  example"
        ]
    }

    // BukkitGradle-26
    void 'when write meta to file - and there are exotic chars in source - should read it correctly'() {
        given:
        source.append($/
            commands:
              퀘스트:
                description: 퀘스트 명령어 입니다.
        /$.stripIndent())

        when:
        CharsetUtils.setDefaultCharset('CP866')
        metaFile.writeTo(target)
        CharsetUtils.setDefaultCharset('UTF-8')

        then:
        target.readLines() == [
                "name: TestPlugin",
                "main: com.example.plugin.Plugin",
                "version: 0.1",
                "commands:",
                "  퀘스트:",
                "    description: 퀘스트 명령어 입니다."
        ]
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
