package ru.endlesscode.bukkitgradle.server

import groovy.xml.MarkupBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.endlesscode.bukkitgradle.BukkitGradlePlugin

class DevServerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        ServerCore serverCore = new ServerCore(project)
        project.task("runServer", type: RunServer, dependsOn: "prepareServer") {
            core serverCore
        }.configure {
            group = BukkitGradlePlugin.GROUP
            description = 'Run dev server'
        }

        project.task("prepareServer", type: PrepareServer, dependsOn: ["build", "copyServerCore"]) {
            core serverCore
        }.configure {
            group = BukkitGradlePlugin.GROUP
            description = 'Prepare server ro run. Configure server and copy compiled plugin to plugins dir'
        }

        project.task("buildIdeaRun", dependsOn: "prepareServer").doLast {
            def runConfigurationsDir = new File(".idea/runConfigurations")
            runConfigurationsDir.mkdirs()

            def prepareServer = project.tasks.prepareServer as PrepareServer
            def run = prepareServer.run
            def taskName = "Run Server"
            def serverDir = prepareServer.serverDir.toString()
            def props = run.javaArgs
            def args = run.bukkitArgs

            def writer = new FileWriter(new File(runConfigurationsDir, "${taskName.replace(" ", "_")}.xml"))
            def xml = new MarkupBuilder(writer)

            xml.component(name: "ProjectRunConfigurationManager") {
                configuration(default: 'false', name: taskName, type: "JarApplication", factoryName: "JAR Application", singleton: "true") {
                    option(name: 'JAR_PATH', value: "$serverDir/core.jar")
                    option(name: 'VM_PARAMETERS', value: props)
                    option(name: 'PROGRAM_PARAMETERS', value: args)
                    option(name: 'WORKING_DIRECTORY', value: serverDir)
                }
            }
        }.configure {
            group = BukkitGradlePlugin.GROUP
            description = 'Configure IDEA server run configuration'
        }
    }
}
