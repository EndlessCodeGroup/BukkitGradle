package ru.endlesscode.bukkitgradle.server.task

import ru.endlesscode.bukkitgradle.PluginSpecification

class CopyServerCoreSpec extends PluginSpecification {

    private final static TASK_NAME = ':copyServerCore'

    def "when run copyServerCore - and selected spigot core - should also run build-tools build"() {
        given: "spigot selected"
        buildFile << """
            bukkit {
                server.core = "spigot"
            }
        """.stripIndent()

        when: "run copyServerCore"
        run(TASK_NAME, '--dry-run')

        then: "should download and run build-tools"
        result.output.startsWith(
                """\
                :downloadBuildTools SKIPPED
                :buildServerCore SKIPPED
                :copyServerCore SKIPPED

                BUILD SUCCESSFUL""".stripIndent()
        )
    }

    def "when run copyServerCore - and selected paper core - should also download paperclip"() {
        given: "paper selected"
        buildFile << """
            bukkit {
                server.core = "paper"
            }
        """.stripIndent()

        when: "run copyServerCore"
        run(TASK_NAME, '--dry-run')

        then: "should download and run build-tools"
        result.output.startsWith(
                """\
                :downloadPaperVersions SKIPPED
                :downloadPaperclip SKIPPED
                :copyServerCore SKIPPED

                BUILD SUCCESSFUL""".stripIndent()
        )
    }
}
