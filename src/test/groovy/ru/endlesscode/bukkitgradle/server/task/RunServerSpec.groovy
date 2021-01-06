package ru.endlesscode.bukkitgradle.server.task


import org.gradle.testkit.runner.TaskOutcome
import ru.endlesscode.bukkitgradle.PluginSpecification

class RunServerSpec extends PluginSpecification {

    private final static TASK_NAME = ':runServer'

    def "when run runServer - should also run task dependencies"() {
        when: "run server"
        run(TASK_NAME, '-x', 'copyServerCore')

        then: "generateRunningScript should be successful"
        taskOutcome(':generateRunningScript') == TaskOutcome.SUCCESS

        and: "prepareServer should be successful"
        taskOutcome(":prepareServer") == TaskOutcome.SUCCESS

        and: "task should be also successful"
        taskOutcome(TASK_NAME) == TaskOutcome.SUCCESS
    }
}
