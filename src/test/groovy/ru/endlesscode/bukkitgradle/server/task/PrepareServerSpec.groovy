package ru.endlesscode.bukkitgradle.server.task

import org.gradle.testkit.runner.TaskOutcome
import ru.endlesscode.bukkitgradle.PluginSpecification

class PrepareServerSpec extends PluginSpecification {

    private final static TASK_NAME = ':prepareServer'

    def "when run prepareServer - should run successfully"() {
        when: "run prepareServer"
        run(TASK_NAME)

        then: "task should be successful"
        taskOutcome(TASK_NAME) == TaskOutcome.SUCCESS
    }

    def "when run prepareServer again - should be up-to-date"() {
        when: "run prepareServer"
        run(TASK_NAME)

        then: "task should be successful"
        taskOutcome(TASK_NAME) == TaskOutcome.SUCCESS

        when: "run prepareServer again"
        run(TASK_NAME)

        then: "task should be up-to-date"
        taskOutcome(TASK_NAME) == TaskOutcome.UP_TO_DATE
    }

    def "when run prepareServer - should set online-mode"() {
        given: "configured online-mode"
        buildFile << """
            bukkit {
                run {
                    onlineMode = false
                }
            }
        """.stripIndent()
        def serverDir = "run/"

        when: "run prepareServer"
        run(TASK_NAME)

        then: "online-mode should be false"
        file("$serverDir/server.properties").readLines().contains("online-mode=false")
    }
}
