package ru.endlesscode.bukkitgradle.server.task

import org.gradle.testkit.runner.TaskOutcome
import ru.endlesscode.bukkitgradle.PluginSpecification

class CreateIdeaJarRunConfigurationSpec extends PluginSpecification {

    private final static TASK_NAME = ':buildIdeaRun'
    private final static IDEA_ACTIVE_PROPERTY = '-Didea.active=true'

    def "when run from IDEA - task should work"() {
        when:
        run(TASK_NAME, IDEA_ACTIVE_PROPERTY)

        then:
        taskOutcome(TASK_NAME) == TaskOutcome.SUCCESS
    }

    def "when run not from IDEA - task should be skipped"() {
        when:
        run(TASK_NAME)

        then:
        taskOutcome(TASK_NAME) == TaskOutcome.SKIPPED
    }

    def "when run from IDEA - should generate xml"() {
        given:
        buildFile << '''
            bukkit {
                version = '1.15.2'
            }
        '''.stripIndent()

        def serverDir = "${project.buildDir}/server/1.15.2"

        when:
        run(TASK_NAME, IDEA_ACTIVE_PROPERTY)

        then:
        file('.idea/runConfigurations/test_plugin__Run_server.xml').text == """\
            <component name='ProjectRunConfigurationManager'>
              <configuration default='false' name='test-plugin: Run server' type='JarApplication' singleton='true'>
                <option name='JAR_PATH' value='${serverDir}/core.jar' />
                <option name='VM_PARAMETERS' value='' />
                <option name='PROGRAM_PARAMETERS' value='' />
                <option name='WORKING_DIRECTORY' value='${serverDir}' />
                <method v='2'>
                  <option name='Gradle.BeforeRunTask' enabled='true' tasks='prepareServer' externalProjectPath='\$PROJECT_DIR\$' />
                </method>
              </configuration>
            </component>
        """.stripIndent().trim()
    }
}
