package ru.endlesscode.bukkitgradle.server.task


import org.gradle.testkit.runner.TaskOutcome
import ru.endlesscode.bukkitgradle.PluginSpecification

class PrepareServerSpec extends PluginSpecification {

    private final static TASK_NAME = ':prepareServer'

    def "when run prepareServer - should also run task dependencies"() {
        when: "run prepareServer"
        run(TASK_NAME, '-x', 'copyServer')

        then: "copyPlugins should be successful"
        taskOutcome(':copyPlugins') == TaskOutcome.SUCCESS

        and: "task should be also successful"
        taskOutcome(TASK_NAME) == TaskOutcome.SUCCESS
    }

    def "when run prepareServer again - should be uo-to-date"() {
        when: "run prepareServer"
        run(TASK_NAME, '-x', 'copyServer')

        then: "task should be successful"
        taskOutcome(TASK_NAME) == TaskOutcome.SUCCESS

        when: "run prepareServer again"
        run(TASK_NAME, '-x', 'copyServer')

        then: "task should be uo-to-date"
        taskOutcome(TASK_NAME) == TaskOutcome.UP_TO_DATE
    }

    def "when run prepareServer - should set eula and online-mode"() {
        given: "configured eula and online-mode"
        buildFile << """
            bukkit {
                version = '1.16.2'
                run {
                    eula = true
                    onlineMode = false
                }
            }
        """.stripIndent()
        def serverDir = "build/server/1.16.2/"

        when: "run prepareServer"
        run(TASK_NAME, '-x', 'copyServer')

        then: "eula should be true"
        file("$serverDir/eula.txt").readLines().contains("eula=true")

        and: "online-mode should be false"
        file("$serverDir/server.properties").readLines().contains("online-mode=false")
    }
}
