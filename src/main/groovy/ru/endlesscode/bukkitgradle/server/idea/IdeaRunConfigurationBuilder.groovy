package ru.endlesscode.bukkitgradle.server.idea

import groovy.xml.MarkupBuilder
import ru.endlesscode.bukkitgradle.extension.RunConfiguration
import ru.endlesscode.bukkitgradle.server.ServerConstants

import java.nio.file.Files
import java.nio.file.Path

class IdeaRunConfigurationBuilder {

    /**
     * Builds and writes to file run configuration in IDEA .xml format
     *
     * @param configurationDir The configurations dir
     */
    static void build(Path configurationDir,
                      Path serverDir,
                      RunConfiguration runConfiguration) {
        if (Files.notExists(configurationDir)) {
            return
        }

        def taskName = 'Run Server'
        def args = runConfiguration.bukkitArgs
        def jvmArgs = runConfiguration.buildJvmArgs(false)

        def runConfigurationFile = configurationDir.resolve("${taskName.replace(' ', '_')}.xml")
        def xml = new MarkupBuilder(runConfigurationFile.newWriter())
        xml.component(name: 'ProjectRunConfigurationManager') {
            configuration(
                    default: 'false',
                    name: taskName,
                    type: 'JarApplication',
                    factoryName: 'JAR Application',
                    singleton: 'true'
            ) {
                option(name: 'JAR_PATH', value: "${serverDir.resolve(ServerConstants.FILE_CORE)}")
                option(name: 'VM_PARAMETERS', value: jvmArgs)
                option(name: 'PROGRAM_PARAMETERS', value: args)
                option(name: 'WORKING_DIRECTORY', value: serverDir)
                envs()
                method {
                    option(
                            name: 'Gradle.BeforeRunTask',
                            enabled: 'true',
                            tasks: 'prepareServer',
                            externalProjectPath: '$PROJECT_DIR$',
                            vmOptions: '',
                            scriptParameters: '')
                }
            }
        }
    }
}
