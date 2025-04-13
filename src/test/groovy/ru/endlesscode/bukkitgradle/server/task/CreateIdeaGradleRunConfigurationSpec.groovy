package ru.endlesscode.bukkitgradle.server.task

import org.gradle.testkit.runner.TaskOutcome
import ru.endlesscode.bukkitgradle.PluginSpecification

class CreateIdeaGradleRunConfigurationSpec extends PluginSpecification {

    private final static TASK_NAME = ':buildIdeaRun'
    private final static IDEA_ACTIVE_PROPERTY = '-Didea.active=true'

    def setup() {
        project.file(".idea").mkdirs()
    }

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
        when:
        run(TASK_NAME, IDEA_ACTIVE_PROPERTY)

        then:
        file('.run/Run Server [test-plugin].run.xml').text == """
            <component name='ProjectRunConfigurationManager'>
              <configuration default='false' name='Run Server [test-plugin]' type='GradleRunConfiguration' factoryName='Gradle' singleton='true'>
                <ExternalSystemSettings>
                  <option name='externalProjectPath' value='\$PROJECT_DIR\$' />
                  <option name='externalSystemIdString' value='GRADLE' />
                  <option name='vmOptions' value='' />
                  <option name='scriptParameters' value='' />
                  <option name='taskNames'>
                    <list>
                      <option value='runServer' />
                    </list>
                  </option>
                </ExternalSystemSettings>
                <ExternalSystemDebugServerProcess>false</ExternalSystemDebugServerProcess>
                <ExternalSystemReattachDebugProcess>true</ExternalSystemReattachDebugProcess>
                <DebugAllEnabled>false</DebugAllEnabled>
                <RunAsTest>false</RunAsTest>
                <method v='2' />
              </configuration>
            </component>
            """.stripIndent().trim()
    }

    def "when run configurationsDir in .idea folder is used - should generate .xml extension"() {
        given:
        buildFile << '''
            tasks.buildIdeaRun {
                configurationsDir = file(".idea/runConfigurations")
            }
        '''.stripIndent()

        when:
        run(TASK_NAME, IDEA_ACTIVE_PROPERTY)

        then:
        file('.idea/runConfigurations/Run Server [test-plugin].xml').exists()
    }
}
