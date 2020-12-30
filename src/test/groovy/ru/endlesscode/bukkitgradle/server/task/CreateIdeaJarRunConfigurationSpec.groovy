package ru.endlesscode.bukkitgradle.server.task

import org.gradle.testkit.runner.TaskOutcome
import ru.endlesscode.bukkitgradle.PluginSpecification

class CreateIdeaJarRunConfigurationSpec extends PluginSpecification {

    private final static TASK_NAME = ':buildIdeaRun'
    private final static IDEA_ACTIVE_PROPERTY = '-Didea.active=true'

    def "when run from IDEA - task should work"() {
        when:
        def result = runner.withArguments(TASK_NAME, IDEA_ACTIVE_PROPERTY).build()

        then:
        result.task(TASK_NAME).outcome == TaskOutcome.SUCCESS
    }

    def "when run not from IDEA - task should be skipped"() {
        when:
        def result = runner.withArguments(TASK_NAME).build()

        then:
        result.task(TASK_NAME).outcome == TaskOutcome.SKIPPED
    }

    def "when run from IDEA - should generate xml"() {
        given:
        buildFile << '''
            bukkit {
                version = '1.15.2'
            }
        '''.stripIndent()

        def serverDir = "${project.buildDir}/server/1.15.2"
        def expectedLines = [
                "<component name='ProjectRunConfigurationManager'>",
                "  <configuration default='false' name='test-plugin: Run server' type='JarApplication' singleton='true'>",
                "    <option name='JAR_PATH' value='${serverDir}/core.jar' />",
                "    <option name='VM_PARAMETERS' value='' />",
                "    <option name='PROGRAM_PARAMETERS' value='' />",
                "    <option name='WORKING_DIRECTORY' value='${serverDir}' />",
                "    <method v='2'>",
                "      <option name='Gradle.BeforeRunTask' enabled='true' tasks='prepareServer' externalProjectPath='\$PROJECT_DIR\$' />",
                "    </method>",
                "  </configuration>",
                "</component>"
        ]

        when:
        runner.withArguments(TASK_NAME, IDEA_ACTIVE_PROPERTY).build()

        and:
        def lines = file('.idea/runConfigurations/test_plugin__Run_server.xml').readLines()

        then:
        expectedLines == lines
    }
}
