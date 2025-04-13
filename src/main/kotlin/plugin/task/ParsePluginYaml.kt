package ru.endlesscode.bukkitgradle.plugin.task

import com.charleskorn.kaml.EmptyYamlDocumentException
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import ru.endlesscode.bukkitgradle.TASKS_GROUP_BUKKIT
import ru.endlesscode.bukkitgradle.plugin.PluginYaml
import ru.endlesscode.bukkitgradle.plugin.extension.PluginConfigurationImpl
import javax.inject.Inject

internal abstract class ParsePluginYaml @Inject constructor(
    providers: ProviderFactory
) : DefaultTask() {

    @get:Internal
    lateinit var yaml: Yaml

    @get:Internal
    lateinit var plugin: PluginConfigurationImpl

    @get:Optional
    @get:InputFile
    abstract val pluginYamlFile: RegularFileProperty

    private var _pluginYaml: PluginYaml? = null

    @get:Internal
    val pluginYaml: Provider<PluginYaml> = providers.provider { checkNotNull(_pluginYaml) }

    init {
        group = TASKS_GROUP_BUKKIT
        description = "Parse plugin.yml file if it exists"
    }

    @TaskAction
    fun parse() {
        _pluginYaml = readFromFile()
    }

    /** Reads plugin.yaml from [pluginYamlFile] and adds conventions for specified fields. */
    private fun readFromFile(): PluginYaml {
        if (!pluginYamlFile.isPresent) return PluginYaml()

        val file = pluginYamlFile.get().asFile
        val pluginYaml = try {
            file.inputStream().use { yaml.decodeFromStream<PluginYaml>(it) }
        } catch (cause: EmptyYamlDocumentException) {
            return PluginYaml()
        }

        return pluginYaml.apply {
            main?.let(plugin.main::convention)
            name?.let(plugin.name::convention)
            description?.let(plugin.description::convention)
            version?.let(plugin.version::convention)
            apiVersion?.let(plugin.apiVersion::convention)
            website?.let(plugin.url::convention)
            authors?.let(plugin.authors::convention)
        }
    }
}
