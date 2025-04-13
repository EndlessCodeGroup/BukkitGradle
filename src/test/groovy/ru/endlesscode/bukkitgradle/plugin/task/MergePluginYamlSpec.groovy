package ru.endlesscode.bukkitgradle.plugin.task


import org.gradle.testkit.runner.TaskOutcome
import ru.endlesscode.bukkitgradle.PluginSpecification
import ru.endlesscode.bukkitgradle.plugin.PluginConfigurationPlugin
import ru.endlesscode.bukkitgradle.util.CharsetUtils

class MergePluginYamlSpec extends PluginSpecification {

    private final static MERGE_PLUGIN_YAML = ':mergePluginYaml'

    private File sourcePluginYamlFile
    private File pluginYamlFile

    def setup() {
        sourcePluginYamlFile = file("src/main/resources/$PluginConfigurationPlugin.FILE_NAME")
        pluginYamlFile = file("build/tmp/mergePluginYaml/$PluginConfigurationPlugin.FILE_NAME")

        buildFile << """
            bukkit.apiVersion = "1.16.2"
        """.stripIndent()
    }

    def 'when run processResources - should also run mergePluginYaml'() {
        when:
        run(':processResources')

        then:
        taskOutcome(MERGE_PLUGIN_YAML) == TaskOutcome.SUCCESS

        and:
        taskOutcome(":parsePluginYaml") == TaskOutcome.SUCCESS
    }

    def 'when run processResources - and plugin.yaml generation disabled - should not run mergePluginYaml'() {
        given: "plugin generation disabled"
        buildFile << "bukkit.plugin.disablePluginYamlGeneration()"

        when: "run processResources"
        run(':processResources')

        then: "task mergePluginYaml completed successfully"
        taskOutcome(MERGE_PLUGIN_YAML) == TaskOutcome.SKIPPED

        and: "task mergePluginYaml completed successfully"
        taskOutcome(":parsePluginYaml") == TaskOutcome.SKIPPED
    }

    def 'when merge plugin.yaml - should generate default plugin.yaml successfully'() {
        when:
        run(MERGE_PLUGIN_YAML)

        then: "plugin file content corresponds to default config"
        pluginYamlFile.text == """
                         main: "com.example.testplugin.TestPlugin"
                         name: "test-plugin"
                         version: "1.0"
                         api-version: "1.16"
                         """.stripIndent().trim()
    }

    def 'when merge plugin.yaml - should set api-version'(String apiVersion, String expectedResult) {
        when: "api version is $apiVersion"
        buildFile << """
            bukkit.apiVersion = "$apiVersion"
        """.stripIndent()

        and:
        run(MERGE_PLUGIN_YAML)

        then: "plugin file content corresponds to default config"
        pluginYamlFile.text == """
                         main: "com.example.testplugin.TestPlugin"
                         name: "test-plugin"
                         version: "1.0"
                         api-version: "$expectedResult"
                         """.stripIndent().trim()

        where:
        apiVersion | expectedResult
        "1.16.5"   | "1.16"
        "1.20.4"   | "1.20"
        "1.20.5"   | "1.20.5"
        "1.21.1"   | "1.21.1"
    }

    def 'when merge plugin.yaml - and generate it again - should skip second task run'() {
        when:
        run(MERGE_PLUGIN_YAML)

        and: "run generate again"
        run(MERGE_PLUGIN_YAML)

        then: "the task is skipped due to up-to-date"
        taskOutcome(MERGE_PLUGIN_YAML) == TaskOutcome.UP_TO_DATE
    }

    def 'when merge plugin.yaml - and changed plugin configuration - should update plugin.yaml'() {
        when:
        run(MERGE_PLUGIN_YAML)

        and: "changed description"
        buildFile << 'description = "Plugin can has description"'

        and: "run the task again"
        run(MERGE_PLUGIN_YAML)

        then: "the task is successful"
        taskOutcome(MERGE_PLUGIN_YAML) == TaskOutcome.SUCCESS

        and: "plugin generated with new description"
        pluginYamlFile.text == """
                         main: "com.example.testplugin.TestPlugin"
                         name: "test-plugin"
                         description: "Plugin can has description"
                         version: "1.0"
                         api-version: "1.16"
                         """.stripIndent().trim()
    }

    void 'when merge plugin.yaml - and all properties configured - should write all lines'() {
        given: "configured all plugin properties"
        //language=kotlin
        buildFile << """
            bukkit {
                plugin {
                    name.set("TestPlugin")
                    description.set("Test plugin description")
                    main.set("com.example.plugin.Plugin")
                    version.set("0.1")
                    apiVersion.set("1.13")
                    url.set("https://www.example.com/")
                    authors.set(listOf("OsipXD", "Contributors"))
                }
            }
        """.stripIndent()

        when: "run processResources"
        run(MERGE_PLUGIN_YAML)

        then: "should write all lines"
        pluginYamlFile.text == """
                         main: "com.example.plugin.Plugin"
                         name: "TestPlugin"
                         description: "Test plugin description"
                         version: "0.1"
                         api-version: "1.13"
                         authors: ["OsipXD", "Contributors"]
                         website: "https://www.example.com/"
                         """.stripIndent().trim()
    }

    void 'when merge plugin.yaml - and all properties configured with assignment - should write all lines'() {
        given: "configured all plugin properties in old way"
        //language=kotlin
        buildFile << """
            bukkit {
                plugin {
                    name = "TestPlugin"
                    description = "Test plugin description"
                    main = "com.example.plugin.Plugin"
                    version = "0.1"
                    url = "https://www.example.com/"
                    authors = listOf("OsipXD", "Contributors")
                }
            }
        """.stripIndent()

        when: "run processResources"
        run(MERGE_PLUGIN_YAML)

        then: "should write all lines"
        pluginYamlFile.text == """
                         main: "com.example.plugin.Plugin"
                         name: "TestPlugin"
                         description: "Test plugin description"
                         version: "0.1"
                         api-version: "1.16"
                         authors: ["OsipXD", "Contributors"]
                         website: "https://www.example.com/"
                         """.stripIndent().trim()
    }

    void 'when merge plugin.yaml - and there are extra fields in source - should write all lines'() {
        given: "source plugin file with extra fields"
        sourcePluginYamlFile << """
            depend: [Vault, ProtocolLib]
            commands:
              example:
                description: Just a command
            permissions:
              example.foo:
                description: My foo permission
        """.stripIndent()

        when: "run processResources"
        run(MERGE_PLUGIN_YAML)

        then: "should write plugin with the extra fields"
        pluginYamlFile.text == """
                         main: "com.example.testplugin.TestPlugin"
                         name: "test-plugin"
                         version: "1.0"
                         api-version: "1.16"
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
    void 'when merge plugin.yaml - and there are exotic chars in source - should read it correctly'() {
        given: "source plugin file with exotic chars"
        sourcePluginYamlFile << """
            commands:
              퀘스트:
                description: 퀘스트 명령어 입니다.
        """.stripIndent()

        and: "default charset differs from UTF-8"
        CharsetUtils.setDefaultCharset('CP866')

        when: "run processResources"
        run(MERGE_PLUGIN_YAML)
        CharsetUtils.setDefaultCharset('UTF-8')

        then:
        pluginYamlFile.text == """
                         main: "com.example.testplugin.TestPlugin"
                         name: "test-plugin"
                         version: "1.0"
                         api-version: "1.16"
                         commands:
                           "퀘스트":
                             description: "퀘스트 명령어 입니다."
                         """.stripIndent().trim()
    }

    void 'when merge plugin.yaml - and there are fields in source - should prefer values from source'() {
        given: "source plugin file with extra fields"
        sourcePluginYamlFile << """
            name: SourceValue
            version: 1.2
        """.stripIndent()

        when: "run processResources"
        run(MERGE_PLUGIN_YAML)

        then: "should write plugin and prefer source fields"
        pluginYamlFile.text == """
                         main: "com.example.testplugin.SourceValue"
                         name: "SourceValue"
                         version: "1.2"
                         api-version: "1.16"
                         """.stripIndent().trim()
    }

    void 'when merge plugin.yaml - and there are conflicting fields in source and in build script - should prefer values from build script'() {
        given: "source plugin.yaml file with extra fields"
        sourcePluginYamlFile << """
            name: SourceValue
            version: 1.2
        """.stripIndent()

        and: "conflicting fields in build script"
        buildFile << """
            bukkit {
                plugin {
                    name.set("BuildscriptValue")
                    version.set("1.3")
                }
            }
        """.stripIndent()

        when:
        run(MERGE_PLUGIN_YAML)

        then: "should overwrite source fields"
        pluginYamlFile.text == """
                         main: "com.example.testplugin.BuildscriptValue"
                         name: "BuildscriptValue"
                         version: "1.3"
                         api-version: "1.16"
                         """.stripIndent().trim()
    }
}
