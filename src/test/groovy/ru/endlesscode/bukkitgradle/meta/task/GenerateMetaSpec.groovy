package ru.endlesscode.bukkitgradle.meta.task

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.endlesscode.bukkitgradle.PluginSpecification
import ru.endlesscode.bukkitgradle.meta.MetaFile

class GenerateMetaSpec extends PluginSpecification {

    private final static TASK_PATH = ':generatePluginMeta'

    private File metaFile

    def setup() {
        metaFile = file("build/tmp/generatePluginMeta/$MetaFile.NAME")
    }

    def 'generate default plugin meta'() {
        when:
        def result = generatePluginMeta()

        then:
        result.task(TASK_PATH).outcome == TaskOutcome.SUCCESS

        and:
        metaFile.text == """\
            name: test-plugin
            main: com.example.testplugin.TestPlugin
            version: 1.0
        """.stripIndent()
    }

    private BuildResult generatePluginMeta() {
        return runner.withArguments(TASK_PATH).build()
    }
}
