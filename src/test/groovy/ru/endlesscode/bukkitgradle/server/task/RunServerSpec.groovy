package ru.endlesscode.bukkitgradle.server.task

import ru.endlesscode.bukkitgradle.BukkitGradlePlugin
import ru.endlesscode.bukkitgradle.PluginSpecification

class RunServerSpec extends PluginSpecification {

    private final static TASK_NAME = 'runServer'

    def "when apply plugin - runServer task should depend on prepareServer"() {
        given: "bukkit gradle plugin applied"
        project.apply(plugin: BukkitGradlePlugin)
        def task = project.tasks.named(TASK_NAME).get()

        expect: "runServer depends on prepareServer"
        task.dependsOn.collect { it.name } == ["prepareServer"]
    }

    def "when toolchain is newer than server minimal Java - should run server with toolchain Java"() {
        given: "server requires Java 8, but plugin is compiled with Java 17"
        //language=kotlin
        buildFile << """
            bukkit.apiVersion = "1.16.5"
            java.toolchain.languageVersion = JavaLanguageVersion.of(17)

            tasks.register("printServerJava") {
                val launcher = tasks.named<xyz.jpenilla.runpaper.task.RunServer>("$TASK_NAME")
                    .flatMap { it.javaLauncher }
                doLast { println("Server Java: " + launcher.get().metadata.languageVersion) }
            }
        """.stripIndent()

        when:
        run("printServerJava")

        then: "server runs with Java 17"
        result.output.contains("Server Java: 17")
    }
}
