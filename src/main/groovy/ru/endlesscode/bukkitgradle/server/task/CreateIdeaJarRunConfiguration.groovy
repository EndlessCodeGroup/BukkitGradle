package ru.endlesscode.bukkitgradle.server.task

import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import ru.endlesscode.bukkitgradle.BukkitGradlePlugin
import ru.endlesscode.bukkitgradle.server.util.Idea

/**
 * Builds and writes to file run configuration in IDEA .xml format
 */
class CreateIdeaJarRunConfiguration extends DefaultTask {

    @Input
    final Property<String> configurationName = project.objects.property(String)

    @Input
    final Property<String> vmParameters = project.objects.property(String)

    @Input
    final Property<String> programParameters = project.objects.property(String)

    @Input
    final Property<String> beforeRunTask = project.objects.property(String)

    @Input
    final Property<String> jarPath = project.objects.property(String)

    @OutputDirectory
    final DirectoryProperty configurationsDir = project.objects.directoryProperty()

    CreateIdeaJarRunConfiguration() {
        group = BukkitGradlePlugin.GROUP
        description = 'Configure server run configuration for IDEA'

        vmParameters.convention('')
        programParameters.convention('')

        onlyIf { Idea.isActive() }
    }

    @TaskAction
    def createJarRunConfiguration() {
        def configurationName = configurationName.get()
        def configurationsDir = configurationsDir.get()

        configurationsDir.asFile.mkdirs()

        def runConfigurationFile = configurationsDir
                .file("${Idea.fileNameSlug(configurationName)}.xml")
                .asFile

        def xml = new MarkupBuilder(runConfigurationFile.newWriter('UTF-8'))
        xml.component(name: 'ProjectRunConfigurationManager') {
            configuration(
                    default: 'false',
                    name: configurationName,
                    type: 'JarApplication',
                    singleton: 'true'
            ) {
                option(name: 'JAR_PATH', value: jarPath.get())
                option(name: 'VM_PARAMETERS', value: vmParameters.get())
                option(name: 'PROGRAM_PARAMETERS', value: programParameters.get())
                option(name: 'WORKING_DIRECTORY', value: new File(jarPath.get()).parentFile)
                method(v: 2) {
                    option(name: 'Gradle.BeforeRunTask',
                            enabled: 'true',
                            tasks: beforeRunTask.get(),
                            externalProjectPath: '$PROJECT_DIR$')
                }
            }
        }
    }
}
