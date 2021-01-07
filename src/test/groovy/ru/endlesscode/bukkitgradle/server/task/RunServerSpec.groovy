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
}
