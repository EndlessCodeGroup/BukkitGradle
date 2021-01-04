package ru.endlesscode.bukkitgradle.server.task

import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.withGroovyBuilder
import ru.endlesscode.bukkitgradle.TASKS_GROUP_BUKKIT
import ru.endlesscode.bukkitgradle.server.util.Idea
import java.io.File

/** Builds and writes to file run configuration in IDEA .xml format. */
public open class CreateIdeaJarRunConfiguration : DefaultTask() {

    @Input
    public val configurationName: Property<String> = project.objects.property()

    @Input
    public val vmParameters: Property<String> = project.objects.property()

    @Input
    public val programParameters: Property<String> = project.objects.property()

    @Input
    public val beforeRunTask: Property<String> = project.objects.property()

    @Input
    public val jarPath: Property<File> = project.objects.property()

    @OutputDirectory
    public val configurationsDir: DirectoryProperty = project.objects.directoryProperty()

    init {
        group = TASKS_GROUP_BUKKIT
        description = "Configure server run configuration for IDEA"

        vmParameters.convention("")
        programParameters.convention("")

        onlyIf { Idea.isActive() }
    }

    @TaskAction
    public fun createJarRunConfiguration() {
        val configurationName = configurationName.get()
        val configurationsDir = configurationsDir.get()

        configurationsDir.asFile.mkdirs()

        val runConfigurationFile = configurationsDir
            .file("${Idea.fileNameSlug(configurationName)}.xml")
            .asFile

        MarkupBuilder(runConfigurationFile.writer()).withGroovyBuilder {
            "component"("name" to "ProjectRunConfigurationManager") {
                "configuration"(
                    "default" to false,
                    "name" to configurationName,
                    "type" to "JarApplication",
                    "singleton" to true
                ) {
                    "option"("name" to "JAR_PATH", "value" to jarPath.get())
                    "option"("name" to "VM_PARAMETERS", "value" to vmParameters.get())
                    "option"("name" to "PROGRAM_PARAMETERS", "value" to programParameters.get())
                    "option"("name" to "WORKING_DIRECTORY", "value" to jarPath.get().parentFile)
                    "method"("v" to 2) {
                        "option"(
                            "name" to "Gradle.BeforeRunTask",
                            "enabled" to true,
                            "tasks" to beforeRunTask.get(),
                            "externalProjectPath" to "\$PROJECT_DIR$"
                        )
                    }
                }
            }
        }
    }
}
