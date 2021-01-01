package ru.endlesscode.bukkitgradle.meta.task

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.endlesscode.bukkitgradle.PluginSpecification
import ru.endlesscode.bukkitgradle.meta.MetaFile
import ru.endlesscode.bukkitgradle.util.CharsetUtils

class GenerateMetaSpec extends PluginSpecification {

    private final static TASK_PATH = ':generatePluginMeta'

    private File sourceMetaFile
    private File metaFile

    def setup() {
        sourceMetaFile = file("src/main/resources/$MetaFile.NAME")
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

    void 'when generate meta - and there is filled meta file - should keep only unsupported lines in the file'() {
        given: "filled meta file"
        sourceMetaFile << """
            name: TestPlugin
            description: Test plugin description
            version: 0.1

            main: com.example.plugin.Plugin
            author: OsipXD
            website: www.example.com

            depend: [Vault, ProtocolLib]
            command:
              example
        """.stripIndent()

        when: "run processResources"
        def result = generatePluginMeta()

        then: "the task is successful"
        result.task(TASK_PATH).outcome == TaskOutcome.SUCCESS

        and: "should keep only unsupported lines in source file"
        sourceMetaFile.text == """\
            depend: [Vault, ProtocolLib]
            command:
              example
        """.stripIndent()
    }

    void 'when generate meta - and all properties configured - should write all lines'() {
        given: "configured all meta properties"
        //language=gradle
        buildFile << """
            bukkit {
                meta {
                    name.set('TestPlugin')
                    description.set('Test plugin description')
                    main.set('com.example.plugin.Plugin')
                    version.set('0.1')
                    url.set('http://www.example.com/')
                    authors.set(["OsipXD", "Contributors"])
                }
            }
        """.stripIndent()

        when: "run processResources"
        def result = generatePluginMeta()

        then: "the task is successful"
        result.task(TASK_PATH).outcome == TaskOutcome.SUCCESS

        and: "should write all lines"
        metaFile.text == """\
            name: TestPlugin
            description: Test plugin description
            main: com.example.plugin.Plugin
            version: 0.1
            website: http://www.example.com/
            authors: [OsipXD, Contributors]
        """.stripIndent()
    }

    void 'when generate meta - and there are extra fields in source - should write all lines'() {
        given: "source meta file with extra fields"
        sourceMetaFile << """
            depend: [Vault, ProtocolLib]
            command:
              example
        """.stripIndent()

        when: "run processResources"
        def result = generatePluginMeta()

        then: "the task is successful"
        result.task(TASK_PATH).outcome == TaskOutcome.SUCCESS

        and: "should write meta with the extra fields"
        metaFile.text == """\
            name: test-plugin
            main: com.example.testplugin.TestPlugin
            version: 1.0
            depend: [Vault, ProtocolLib]
            command:
              example
        """.stripIndent()
    }

    // BukkitGradle-26
    void 'when generate meta - and there are exotic chars in source - should read it correctly'() {
        given: "source meta file with exotic chars"
        sourceMetaFile << """
            commands:
              퀘스트:
                description: 퀘스트 명령어 입니다.
        """.stripIndent()

        and: "default charset differs from UTF-8"
        CharsetUtils.setDefaultCharset('CP866')

        when: "run processResources"
        def result = generatePluginMeta()
        CharsetUtils.setDefaultCharset('UTF-8')

        then: "the task is successful"
        result.task(TASK_PATH).outcome == TaskOutcome.SUCCESS

        and:
        metaFile.text == """\
            name: test-plugin
            main: com.example.testplugin.TestPlugin
            version: 1.0
            commands:
              퀘스트:
                description: 퀘스트 명령어 입니다.
        """.stripIndent()
    }

    private BuildResult generatePluginMeta() {
        return runner.withArguments(TASK_PATH).build()
    }
}
