package ru.endlesscode.bukkitgradle.server.task

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.endlesscode.bukkitgradle.PluginSpecification

class RunServerSpec extends PluginSpecification {

    private final static TASK_NAME = ':runServer'

    def "when run runServer - should also task dependencies"() {
        when: "run server"
        def result = runServer()

        then: "task should be successful"
        result.task(TASK_NAME).outcome == TaskOutcome.SUCCESS
    }

    private BuildResult runServer() {
        return runner.withArguments(TASK_NAME, "--stacktrace").build()
    }
}
