package ru.endlesscode.bukkitgradle.plugin

import com.charleskorn.kaml.*
import kotlinx.serialization.decodeFromString
import org.gradle.api.GradleException
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml
import xyz.jpenilla.resourcefactory.bukkit.Permission

private val yaml by lazy {
    Yaml(
        configuration = YamlConfiguration(
            strictMode = false,
            decodeEnumCaseInsensitive = true,
            yamlNamingStrategy = YamlNamingStrategy.KebabCase,
        )
    )
}

/** Parses the source plugin.yml [content] and uses its values as conventions. */
internal fun BukkitPluginYaml.applyConventionsFromSource(content: String) {
    val defaults = parseDefaults(content)
    if (defaults != null) applyConventions(defaults)
}

private fun parseDefaults(content: String): BukkitPluginYamlDefaults? {
    return try {
        yaml.decodeFromString<BukkitPluginYamlDefaults>(content)
    } catch (_: EmptyYamlDocumentException) {
        null
    } catch (cause: Exception) {
        throw GradleException("Failed to parse plugin.yml: ${cause.message}.\n" +
            "Ensure the file is valid YAML that matches plugin.yml schema.", cause)
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
