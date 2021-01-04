package ru.endlesscode.bukkitgradle.server.task

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.endlesscode.bukkitgradle.PluginSpecification

class GenerateRunningScriptSpec extends PluginSpecification {

    private final static TASK_NAME = ':generateRunningScript'
    private final static OS_NAME_ARG = '-Dos.name='

    private File scriptDir

    def setup() {
        buildFile << """
            bukkit {
                version = '1.16.4'
                run.debug = false
            }
        """.stripIndent()
        scriptDir = dir("build/server/1.16.4/")
    }

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

    def "when run script generation - and os is windows - should generate .bat file"() {
        when: "run script generation with os windows"
        def result = generateRunningScript('windows')

        then: "task should be successful"
        result.task(TASK_NAME).outcome == TaskOutcome.SUCCESS

        and: "should generate bat script"
        def scriptFile = new File(scriptDir, "start.bat")
        //language=bat
        scriptFile.text == """\
            @echo off
            java -Dfile.encoding=UTF-8 -Xmx1G -jar core.jar
            pause
            exit
        """.stripIndent().trim()
    }

    def "when run script generation - and os is linux - should generate .sh file"() {
        when: "run script generation with os linux"
        def result = generateRunningScript('linux')

        then: "task should be successful"
        result.task(TASK_NAME).outcome == TaskOutcome.SUCCESS

        and: "should generate bat script"
        def scriptFile = new File(scriptDir, "start.sh")
        //language=sh
        scriptFile.text == """\
            #!/usr/bin/env bash

            cd "\$( dirname "\$0" )"
            java -Dfile.encoding=UTF-8 -Xmx1G -jar core.jar
        """.stripIndent().trim()
    }

    private BuildResult generateRunningScript(String osName = null) {
        def arguments = [TASK_NAME]
        if (osName != null) arguments += "$OS_NAME_ARG$osName".toString()
        return runner.withArguments(arguments).build()
    }
}
