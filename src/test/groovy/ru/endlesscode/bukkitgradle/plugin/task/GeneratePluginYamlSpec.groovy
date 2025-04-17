package ru.endlesscode.bukkitgradle.plugin.task


import org.gradle.testkit.runner.TaskOutcome
import ru.endlesscode.bukkitgradle.PluginSpecification
import ru.endlesscode.bukkitgradle.plugin.PluginYamlFeatureKt
import ru.endlesscode.bukkitgradle.util.CharsetUtils

class GeneratePluginYamlSpec extends PluginSpecification {

    private final static RESOURCE_FACTORY = ':mainResourceFactory'
    private final static PARSE_PLUGIN_YAML = ':parsePluginYaml'

    private File pluginYamlFile

    def setup() {
        pluginYamlFile = file("src/main/resources/$PluginYamlFeatureKt.PLUGIN_YML")

        buildFile << """
            bukkit.apiVersion = "1.16.2"
        """.stripIndent()
    }

    def 'when run processResources - should also run resource factory'() {
        when:
        run(':processResources')

        then:
        taskOutcome(RESOURCE_FACTORY) == TaskOutcome.SUCCESS

        and:
        taskOutcome(PARSE_PLUGIN_YAML) == TaskOutcome.SUCCESS
    }

    def 'when run processResources - and plugin.yml generation disabled - should not run resource factory'() {
        given:
        buildFile << "bukkit.generatePluginYaml = false"

        when:
        run(':processResources')

        then:
        taskOutcome(RESOURCE_FACTORY) == TaskOutcome.SKIPPED

        and:
        taskOutcome(PARSE_PLUGIN_YAML) == null
    }

    def 'when run processResources - and plugin.yml doesnt exist - should skip parsing'() {
        given:
        pluginYamlFile.delete()

        when:
        run(':processResources')

        then:
        taskOutcome(RESOURCE_FACTORY) == TaskOutcome.SUCCESS

        and:
        taskOutcome(PARSE_PLUGIN_YAML) == null
    }

    def 'when run parsing - and plugin.yml doesnt exist - should show NO_SOURCE'() {
        given:
        pluginYamlFile.delete()

        when:
        run(PARSE_PLUGIN_YAML)

        then:
        taskOutcome(PARSE_PLUGIN_YAML) == TaskOutcome.NO_SOURCE
    }

    def 'when generate plugin.yml - should generate default plugin.yml successfully'() {
        when:
        run(RESOURCE_FACTORY)

        then: "plugin file content corresponds to default config"
        pluginYamlFile.text == """\
            api-version: '1.16'
            name: test-plugin
            version: '1.0'
            main: com.example.testplugin.TestPlugin
            """.stripIndent()
    }

    def 'when generate plugin.yml - should set api-version'(String apiVersion, String expectedResult) {
        when: "api version is $apiVersion"
        buildFile << """
            bukkit.apiVersion = "$apiVersion"
        """.stripIndent()

        and:
        run(RESOURCE_FACTORY)

        then: "plugin file content corresponds to default config"
        pluginYamlFile.text == """\
            api-version: $expectedResult
            name: test-plugin
            version: '1.0'
            main: com.example.testplugin.TestPlugin
            """.stripIndent()

        where:
        apiVersion | expectedResult
        "1.16.5"   | "'1.16'"
        "1.20.4"   | "'1.20'"
        "1.20.5"   | "1.20.5"
        "1.21.1"   | "1.21.1"
    }

    def 'when generate plugin.yml - and generate it again - should skip second task run'() {
        when:
        run(RESOURCE_FACTORY)

        and: "run generation again"
        run(RESOURCE_FACTORY)

        then:
        taskOutcome(RESOURCE_FACTORY) == TaskOutcome.UP_TO_DATE
    }

    def 'when generate plugin.yml - and changed plugin configuration - should update plugin.yml'() {
        when:
        run(RESOURCE_FACTORY)

        and: "changed description"
        buildFile << 'description = "Plugin description goes here"'

        and:
        run(RESOURCE_FACTORY)

        then:
        taskOutcome(RESOURCE_FACTORY) == TaskOutcome.SUCCESS

        and: "plugin generated with new description"
        pluginYamlFile.text == """\
            api-version: '1.16'
            name: test-plugin
            version: '1.0'
            main: com.example.testplugin.TestPlugin
            description: Plugin description goes here
            """.stripIndent()
    }

    void 'when generate plugin.yml - and properties configured using set'() {
        given:
        //language=kotlin
        buildFile << """
            bukkit {
                plugin {
                    name.set("TestPlugin")
                    description.set("Test plugin description")
                    main.set("com.example.plugin.Plugin")
                    version.set("0.1")
                    apiVersion.set("1.13")
                    website.set("https://www.example.com/")
                    authors.set(listOf("OsipXD", "Contributors"))
                }
            }
        """.stripIndent()

        when:
        run(RESOURCE_FACTORY)

        then:
        pluginYamlFile.text == """\
            api-version: '1.13'
            name: TestPlugin
            version: '0.1'
            main: com.example.plugin.Plugin
            description: Test plugin description
            authors:
            - OsipXD
            - Contributors
            website: https://www.example.com/
            """.stripIndent()
    }

    void 'when generate plugin.yml - and properties configured using assignment'() {
        given:
        //language=kotlin
        buildFile << """
            bukkit {
                plugin {
                    name = "TestPlugin"
                    description = "Test plugin description"
                    main = "com.example.plugin.Plugin"
                    version = "0.1"
                    apiVersion = "1.13"
                    website = "https://www.example.com/"
                    authors = listOf("OsipXD", "Contributors")
                }
            }
        """.stripIndent()

        when:
        run(RESOURCE_FACTORY)

        then:
        pluginYamlFile.text == """\
            api-version: '1.13'
            name: TestPlugin
            version: '0.1'
            main: com.example.plugin.Plugin
            description: Test plugin description
            authors:
            - OsipXD
            - Contributors
            website: https://www.example.com/
            """.stripIndent()
    }

    void 'when generate plugin.yml - should parse commands and permissions'() {
        given:
        pluginYamlFile << """\
            depend: [Vault, ProtocolLib]
            default-permission: not op
            commands:
              example:
                description: Just a command
            permissions:
              example.foo:
                description: My foo permission
            """.stripIndent()

        when:
        run(RESOURCE_FACTORY)

        then:
        pluginYamlFile.text == """\
            api-version: '1.16'
            name: test-plugin
            version: '1.0'
            main: com.example.testplugin.TestPlugin
            depend:
            - Vault
            - ProtocolLib
            default-permission: not op
            commands:
                example:
                    description: Just a command
            permissions:
                example.foo:
                    description: My foo permission
            """.stripIndent()
    }

    // BukkitGradle-26
    void 'when generate plugin.yml - and there are exotic chars in source - should read it correctly'() {
        given: "source plugin file with exotic chars"
        pluginYamlFile << """\
            commands:
              퀘스트:
                description: 퀘스트 명령어 입니다.
            """.stripIndent()

        and: "default charset differs from UTF-8"
        CharsetUtils.setDefaultCharset('CP866')

        when: "run processResources"
        try {
            run(RESOURCE_FACTORY)
        } finally {
            CharsetUtils.setDefaultCharset('UTF-8')
        }

        then:
        pluginYamlFile.text == """\
            api-version: '1.16'
            name: test-plugin
            version: '1.0'
            main: com.example.testplugin.TestPlugin
            commands:
                퀘스트:
                    description: 퀘스트 명령어 입니다.
            """.stripIndent()
    }

    void 'when generate plugin.yml - and there are fields in source - should prefer values from source'() {
        given: "plugin.yml file with some fields"
        pluginYamlFile << """\
            name: SourceValue
            version: 1.2
            """.stripIndent()

        when: "run processResources"
        run(RESOURCE_FACTORY)

        then: "should write plugin and keep values of the source file"
        pluginYamlFile.text == """\
            api-version: '1.16'
            name: SourceValue
            version: '1.2'
            main: com.example.testplugin.SourceValue
            """.stripIndent()
    }

    void 'when generate plugin.yml - and there are conflicting values - should prefer values from build script'() {
        given:
        pluginYamlFile << """\
            name: SourceValue
            version: 1.2
            commands:
              source-command:
                description: Should be ignored
            permissions:
              source.permission:
                description: Should be ignored
            """.stripIndent()

        and: "conflicting fields in build script"
        buildFile << """
            bukkit {
                plugin {
                    name = "BuildscriptValue"
                    version = "1.3"
                    commands {
                        register("buildscript-command") {
                            description = "A command from build script"
                        }
                    }
                    permissions {
                        register("buildscript.permission") {
                            description = "A permission from build script"
                        }
                    }
                }
            }
        """.stripIndent()

        when:
        run(RESOURCE_FACTORY)

        then: "should overwrite source fields"
        pluginYamlFile.text == """\
            api-version: '1.16'
            name: BuildscriptValue
            version: '1.3'
            main: com.example.testplugin.BuildscriptValue
            commands:
                buildscript-command:
                    description: A command from build script
            permissions:
                buildscript.permission:
                    description: A permission from build script
            """.stripIndent()
    }

    void 'when generate plugin.yml - and it is already full of values - should keep existing file'() {
        given:
        def content = """\
            api-version: '1.13'
            name: TestPlugin
            version: '0.1'
            main: com.example.plugin.Plugin
            description: Test plugin description
            load: STARTUP
            author: MainAuthor
            authors:
            - OsipXD
            - Contributors
            website: https://www.example.com/
            depend:
            - Dependency1
            - Dependency2
            softdepend:
            - SoftDependency1
            - SoftDependency2
            loadbefore:
            - LoadBefore1
            - LoadBefore2
            prefix: TestPrefix
            default-permission: op
            provides:
            - FeatureA
            - FeatureB
            libraries:
            - org.example:library:1.0
            - com.test:util:2.0
            commands:
                test:
                    description: A test command
                    aliases:
                    - t
                    - tst
                    permission: test.command
                    permission-message: You need permission
                    usage: /test
            permissions:
                test.command:
                    description: Existing command permission
                    default: op
                    children:
                        test.command.child: true
            folia-supported: true
            paper-plugin-loader: custom.plugin.Loader
            paper-skip-libraries: false
            """.stripIndent()
        pluginYamlFile << content

        when:
        run(RESOURCE_FACTORY)

        then: "the file content should remain the same"
        pluginYamlFile.text == content
    }
}
