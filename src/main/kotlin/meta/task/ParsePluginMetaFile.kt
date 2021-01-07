package ru.endlesscode.bukkitgradle.meta.task

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import meta.PluginMetaYaml
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import ru.endlesscode.bukkitgradle.TASKS_GROUP_BUKKIT
import ru.endlesscode.bukkitgradle.meta.extension.PluginMetaImpl
import javax.inject.Inject

internal open class ParsePluginMetaFile @Inject constructor(
    objects: ObjectFactory
) : DefaultTask() {

    @Internal
    lateinit var yaml: Yaml

    @Internal
    lateinit var meta: PluginMetaImpl

    @Optional
    @InputFile
    val metaFile: RegularFileProperty = objects.fileProperty()

    @Internal
    val pluginMetaYaml: Provider<PluginMetaYaml> = project.provider { checkNotNull(metaYaml) }

    private var metaYaml: PluginMetaYaml? = null

    init {
        group = TASKS_GROUP_BUKKIT
        description = "Parse plugin.yml file if it exists"
    }

    @TaskAction
    fun parse() {
        metaYaml = readMetaFromFile()
    }

    /** Reads meta from metaFile and adds conventions for specified fields. */
    private fun readMetaFromFile(): PluginMetaYaml {
        if (!metaFile.isPresent) return PluginMetaYaml()

        val text = metaFile.get().asFile.readText()
        if (text.isBlank()) return PluginMetaYaml()

        return yaml.decodeFromString<PluginMetaYaml>(text).apply {
            main?.let(meta.main::convention)
            name?.let(meta.name::convention)
            description?.let(meta.description::convention)
            version?.let(meta.version::convention)
            apiVersion?.let(meta.apiVersion::convention)
            website?.let(meta.url::convention)
            authors?.let(meta.authors::convention)
        }
    }
}
