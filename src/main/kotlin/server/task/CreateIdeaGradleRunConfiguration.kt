package ru.endlesscode.bukkitgradle.server.task

import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.withGroovyBuilder
import ru.endlesscode.bukkitgradle.TASKS_GROUP_BUKKIT
import ru.endlesscode.bukkitgradle.server.util.Idea
import javax.inject.Inject

/** Builds run configuration in IDEA .xml format and stores it to a file. */
@Suppress("LeakingThis")
public abstract class CreateIdeaGradleRunConfiguration private constructor(
    idea: Idea,
) : DefaultTask() {

    @get:Input
    public abstract val configurationName: Property<String>

    @get:Input
    public abstract val projectPath: Property<String>

    @get:Input
    public abstract val vmOptions: ListProperty<String>

    @get:Input
    public abstract val scriptParameters: ListProperty<String>

    @get:Input
    public abstract val taskNames: ListProperty<String>

    @get:Internal
    public abstract val configurationsDir: DirectoryProperty

    @get:OutputFile
    public val configurationFile: Provider<RegularFile> = configurationsDir
        .zip(configurationName, idea::runConfigurationFile)

    @Suppress("unused")
    @Inject
    internal constructor(objects: ObjectFactory) : this(objects.newInstance<Idea>())

    init {
        group = TASKS_GROUP_BUKKIT
        description = "Configure server run configuration for IDEA"

        vmOptions.convention(emptyList())
        scriptParameters.convention(emptyList())

        configurationsDir.convention(idea.projectDir.map { it.dir(".run") })
        projectPath.convention(idea.relativePathOf(project.projectDir).orElse(project.projectDir.absolutePath))

        val predicate = idea.isActive
        onlyIf { predicate.get() }
    }

    @TaskAction
    public fun createJarRunConfiguration() {
        val configurationName = configurationName.get()
        val configurationFile = checkNotNull(configurationFile.orNull) {
            "Cannot automatically determine the path to run configurations directory. " +
                "Please set 'configurationsDir' manually"
        }.asFile
        configurationFile.parentFile.mkdirs()

        MarkupBuilder(configurationFile.writer()).withGroovyBuilder {
            "component"("name" to "ProjectRunConfigurationManager") {
                "configuration"(
                    "default" to false,
                    "name" to configurationName,
                    "type" to "GradleRunConfiguration",
                    "factoryName" to "Gradle",
                    "singleton" to true,
                ) {
                    "ExternalSystemSettings" {
                        "option"("name" to "externalProjectPath", "value" to projectPath.get())
                        "option"("name" to "externalSystemIdString", "value" to "GRADLE")
                        "option"("name" to "vmOptions", "value" to vmOptions.get().joinToString(" "))
                        "option"("name" to "scriptParameters", "value" to scriptParameters.get().joinToString(" "))
                        "option"("name" to "taskNames") {
                            "list" {
                                for (taskName in taskNames.get()) "option"("value" to taskName)
                            }
                        }
                    }
                    "ExternalSystemDebugServerProcess"(false)
                    "ExternalSystemReattachDebugProcess"(true)
                    "DebugAllEnabled"(false)
                    "RunAsTest"(false)
                    "method"("v" to 2)
                }
            }
        }
    }
}
