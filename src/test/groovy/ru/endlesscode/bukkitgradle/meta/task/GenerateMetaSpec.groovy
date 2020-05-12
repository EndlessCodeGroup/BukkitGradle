package ru.endlesscode.bukkitgradle.meta.task

import ru.endlesscode.bukkitgradle.PluginSpecification
import ru.endlesscode.bukkitgradle.meta.MetaFile

class GenerateMetaSpec extends PluginSpecification {

    File metaFile

    def setup() {
        metaFile = file("build/tmp/generatePluginMeta/$MetaFile.NAME")
    }

    def 'generate default plugin meta'() {
        when:
        runner.withArguments('generatePluginMeta').build()

        then:
        metaFile.text == """
            name: test-plugin
            main: com.example.testplugin.TestPlugin
            version: 1.0
        """.stripIndent()
    }
}
