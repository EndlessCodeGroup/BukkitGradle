package ru.endlesscode.bukkitgradle.plugin.task

import com.charleskorn.kaml.*
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import ru.endlesscode.bukkitgradle.TASKS_GROUP_BUKKIT
import ru.endlesscode.bukkitgradle.plugin.BukkitPluginYamlDefaults
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml
import xyz.jpenilla.resourcefactory.bukkit.Permission

internal abstract class ParsePluginYaml : DefaultTask() {

    private val yaml by lazy {
        Yaml(
            configuration = YamlConfiguration(
                strictMode = false,
                decodeEnumCaseInsensitive = true,
                yamlNamingStrategy = YamlNamingStrategy.KebabCase,
            )
        )
    }

    @get:Internal
    abstract val pluginYaml: Property<BukkitPluginYaml>

    @get:SkipWhenEmpty
    @get:InputFile
    abstract val pluginYamlFile: RegularFileProperty

    init {
        group = TASKS_GROUP_BUKKIT
        description = "Parse plugin.yml file if it exists and add its content as defaults to generation"
    }

    @TaskAction
    fun parseAndSetDefaults() {
        val defaults = readFromFile()
        if (defaults != null) pluginYaml.get().applyConventions(defaults)
    }

    private fun readFromFile(): BukkitPluginYamlDefaults? {
        return try {
            pluginYamlFile.get().asFile
                .inputStream()
                .use { yaml.decodeFromStream<BukkitPluginYamlDefaults>(it) }
        } catch (cause: EmptyYamlDocumentException) {
            logger.debug("plugin.yml is empty – skipping defaults setting", cause)
            null
        } catch (cause: Exception) {
            throw GradleException("Failed to parse plugin.yml: ${cause.message}.\n" +
                "Ensure the file is valid YAML that matches plugin.yml schema.", cause)
        }
    }
}

private fun BukkitPluginYaml.applyConventions(defaults: BukkitPluginYamlDefaults) {
    defaults.main?.let(main::convention)
    defaults.name?.let(name::convention)
    defaults.description?.let(description::convention)
    defaults.prefix?.let(prefix::convention)
    defaults.version?.let(version::convention)
    defaults.apiVersion?.let(apiVersion::convention)
    defaults.load?.let(load::convention)
    defaults.author?.let(author::convention)
    defaults.authors?.let(authors::convention)
    defaults.website?.let(website::convention)
    defaults.depend?.let(depend::convention)
    defaults.softdepend?.let(softDepend::convention)
    defaults.loadbefore?.let(loadBefore::convention)
    defaults.provides?.let(provides::convention)
    defaults.libraries?.let(libraries::convention)
    defaults.defaultPermission?.let(defaultPermission::convention)
    defaults.foliaSupported?.let(foliaSupported::convention)
    defaults.paperPluginLoader?.let(paperPluginLoader::convention)
    defaults.paperSkipLibraries?.let(paperSkipLibraries::convention)

    for ((name, command) in defaults.commands) {
        commands.maybeCreate(name).applyConventions(command)
    }

    for ((name, permission) in defaults.permissions) {
        permissions.maybeCreate(name).applyConventions(permission)
    }
}

private fun BukkitPluginYaml.Command.applyConventions(defaults: BukkitPluginYamlDefaults.Command) {
    defaults.description?.let(description::convention)
    defaults.aliases?.let(aliases::convention)
    defaults.permission?.let(permission::convention)
    defaults.permissionMessage?.let(permissionMessage::convention)
    defaults.usage?.let(usage::convention)
}

private fun Permission.applyConventions(defaults: BukkitPluginYamlDefaults.Permission) {
    defaults.description?.let(description::convention)
    defaults.default?.let(default::convention)
    defaults.children?.let(children::convention)
}
