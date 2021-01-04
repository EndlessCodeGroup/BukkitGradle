package ru.endlesscode.bukkitgradle.server.task

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.endlesscode.bukkitgradle.PluginSpecification

class GenerateRunningScriptSpec extends PluginSpecification {

    private final static TASK_NAME = ':generateRunningScript'

    def "when run script generation - task should generate script"() {
        when: "run script generation"
        def result = generateRunningScript()

        then: "task should be successful"
        result.task(TASK_NAME).outcome == TaskOutcome.SUCCESS
    }

    def "when rerun script generation - and not changing any inputs - task should be up-to-date"() {
        when: "run script generation"
        def result = generateRunningScript()

        then: "task should be successful"
        result.task(TASK_NAME).outcome == TaskOutcome.SUCCESS

        when: "run script generation again"
        result = generateRunningScript()

        then: "task should be up-to-date"
        result.task(TASK_NAME).outcome == TaskOutcome.UP_TO_DATE
    }

    private BuildResult generateRunningScript() {
        return runner.withArguments(TASK_NAME).build()
    }
}
