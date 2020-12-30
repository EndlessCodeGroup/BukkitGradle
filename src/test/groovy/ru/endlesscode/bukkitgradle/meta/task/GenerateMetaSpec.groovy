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

    def 'when run processResources - should also run generatePluginMeta'() {
        when: "run processResources"
        def result = runner.withArguments(':processResources').build()

        then: "task generatePluginMeta completed successfully"
        result.task(TASK_PATH).outcome == TaskOutcome.SUCCESS
    }

    def 'when generate meta - should generate default plugin meta successfully'() {
        when: "run generate meta task"
        def result = generatePluginMeta()

        then: "the task is successful"

        result.task(TASK_PATH).outcome == TaskOutcome.SUCCESS

        and: "meta file content corresponds to default config"
        metaFile.text == """\
            name: test-plugin
            main: com.example.testplugin.TestPlugin
            version: 1.0
        """.stripIndent()
    }

    def 'when generate meta - and generate it again - should skip second task run'() {
        when: "run generate meta task"
        def result = generatePluginMeta()

        then: "the task is successful"
        result.task(TASK_PATH).outcome == TaskOutcome.SUCCESS

        when: "run generate meta again"
        result = generatePluginMeta()

        then: "the task is skipped due to up-to-date"
        result.task(TASK_PATH).outcome == TaskOutcome.UP_TO_DATE
    }

    def 'when generate meta - and generate changed meta - should generate new meta'() {
        when: "run generate meta task"
        def result = generatePluginMeta()

        then: "the task is successful"
        result.task(TASK_PATH).outcome == TaskOutcome.SUCCESS

        when: "change description"
        buildFile.append('description = "Plugin can has description"')

        and: "run generate meta task again"
        result = generatePluginMeta()

        then: "the task is successful"
        result.task(TASK_PATH).outcome == TaskOutcome.SUCCESS

        and: "meta generated with new description"
        metaFile.text == """\
            name: test-plugin
            description: Plugin can has description
            main: com.example.testplugin.TestPlugin
            version: 1.0
        """.stripIndent()
    }

    private BuildResult generatePluginMeta() {
        return runner.withArguments(TASK_PATH).build()
    }
}
