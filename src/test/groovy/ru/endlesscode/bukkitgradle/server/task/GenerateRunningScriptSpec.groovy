package ru.endlesscode.bukkitgradle.server.task


import org.gradle.testkit.runner.TaskOutcome
import ru.endlesscode.bukkitgradle.PluginSpecification

class GenerateRunningScriptSpec extends PluginSpecification {

    private final static TASK_NAME = ':generateRunningScript'

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
        run(TASK_NAME)

        then: "task should be successful"
        taskOutcome(TASK_NAME) == TaskOutcome.SUCCESS
    }

    def "when rerun script generation - and not changing any inputs - task should be up-to-date"() {
        when: "run script generation"
        run(TASK_NAME)

        then: "task should be successful"
        taskOutcome(TASK_NAME) == TaskOutcome.SUCCESS

        when: "run script generation again"
        run(TASK_NAME)

        then: "task should be up-to-date"
        taskOutcome(TASK_NAME) == TaskOutcome.UP_TO_DATE
    }

    def "when run script generation - and os is windows - should generate .bat file"() {
        when: "run script generation with os windows"
        run(TASK_NAME, "-Dos.name=windows")

        then: "task should be successful"
        taskOutcome(TASK_NAME) == TaskOutcome.SUCCESS

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
        run(TASK_NAME, "-Dos.name=linux")

        then: "task should be successful"
        taskOutcome(TASK_NAME) == TaskOutcome.SUCCESS

        and: "should generate bat script"
        def scriptFile = new File(scriptDir, "start.sh")
        //language=sh
        scriptFile.text == """\
            #!/usr/bin/env bash
            set -e

            cd "\$( dirname "\$0" )"
            java -Dfile.encoding=UTF-8 -Xmx1G -jar core.jar
        """.stripIndent().trim()
    }
}
