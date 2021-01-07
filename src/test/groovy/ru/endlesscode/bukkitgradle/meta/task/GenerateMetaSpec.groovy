package ru.endlesscode.bukkitgradle.meta.task


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
        run(':processResources')

        then: "task generatePluginMeta completed successfully"
        taskOutcome(TASK_PATH) == TaskOutcome.SUCCESS
    }

    def 'when generate meta - should generate default plugin meta successfully'() {
        when: "run generate meta task"
        run(TASK_PATH)

        then: "the task is successful"
        taskOutcome(TASK_PATH) == TaskOutcome.SUCCESS

        and: "meta file content corresponds to default config"
        metaFile.text == """\
            main: "com.example.testplugin.TestPlugin"
            name: "test-plugin"
            version: "1.0"
        """.stripIndent().trim()
    }

    def 'when generate meta - and generate it again - should skip second task run'() {
        when: "run generate meta task"
        run(TASK_PATH)

        then: "the task is successful"
        taskOutcome(TASK_PATH) == TaskOutcome.SUCCESS

        when: "run generate meta again"
        run(TASK_PATH)

        then: "the task is skipped due to up-to-date"
        taskOutcome(TASK_PATH) == TaskOutcome.UP_TO_DATE
    }

    def 'when generate meta - and generate changed meta - should generate new meta'() {
        when: "run generate meta task"
        run(TASK_PATH)

        then: "the task is successful"
        taskOutcome(TASK_PATH) == TaskOutcome.SUCCESS

        when: "change description"
        buildFile << 'description = "Plugin can has description"'

        and: "run generate meta task again"
        run(TASK_PATH)

        then: "the task is successful"
        taskOutcome(TASK_PATH) == TaskOutcome.SUCCESS

        and: "meta generated with new description"
        metaFile.text == """\
            main: "com.example.testplugin.TestPlugin"
            name: "test-plugin"
            description: "Plugin can has description"
            version: "1.0"
        """.stripIndent().trim()
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
        run(TASK_PATH)

        then: "the task is successful"
        taskOutcome(TASK_PATH) == TaskOutcome.SUCCESS

        and: "should write all lines"
        metaFile.text == """\
            main: "com.example.plugin.Plugin"
            name: "TestPlugin"
            description: "Test plugin description"
            version: "0.1"
            authors: ["OsipXD", "Contributors"]
            website: "http://www.example.com/"
        """.stripIndent().trim()
    }

    void 'when generate meta - and all properties configured old way - should write all lines'() {
        given: "configured all meta properties in old way"
        //language=gradle
        buildFile << """
            bukkit {
                meta {
                    name = 'TestPlugin'
                    description = 'Test plugin description'
                    main = 'com.example.plugin.Plugin'
                    version = '0.1'
                    url = 'http://www.example.com/'
                    authors = ["OsipXD", "Contributors"]
                }
            }
        """.stripIndent()

        when: "run processResources"
        run(TASK_PATH)

        then: "the task is successful"
        taskOutcome(TASK_PATH) == TaskOutcome.SUCCESS

        and: "should write all lines"
        metaFile.text == """\
            main: "com.example.plugin.Plugin"
            name: "TestPlugin"
            description: "Test plugin description"
            version: "0.1"
            authors: ["OsipXD", "Contributors"]
            website: "http://www.example.com/"
        """.stripIndent().trim()
    }

    void 'when generate meta - and there are extra fields in source - should write all lines'() {
        given: "source meta file with extra fields"
        sourceMetaFile << """
            depend: [Vault, ProtocolLib]
            commands:
              example:
                description: Just a command
            permissions:
              example.foo:
                description: My foo permission
        """.stripIndent()

        when: "run processResources"
        run(TASK_PATH)

        then: "the task is successful"
        taskOutcome(TASK_PATH) == TaskOutcome.SUCCESS

        and: "should write meta with the extra fields"
        metaFile.text == """\
            main: "com.example.testplugin.TestPlugin"
            name: "test-plugin"
            version: "1.0"
            depend: ["Vault", "ProtocolLib"]
            commands:
              "example":
                description: "Just a command"
            permissions:
              "example.foo":
                description: "My foo permission"
        """.stripIndent().trim()
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
        run(TASK_PATH)
        CharsetUtils.setDefaultCharset('UTF-8')

        then: "the task is successful"
        taskOutcome(TASK_PATH) == TaskOutcome.SUCCESS

        and:
        metaFile.text == """\
            main: "com.example.testplugin.TestPlugin"
            name: "test-plugin"
            version: "1.0"
            commands:
              "퀘스트":
                description: "퀘스트 명령어 입니다."
        """.stripIndent().trim()
    }
}
