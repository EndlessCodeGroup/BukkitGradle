package ru.endlesscode.bukkitgradle.plugin.extension

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import org.slf4j.LoggerFactory
import ru.endlesscode.bukkitgradle.extensions.finalizedOnRead
import ru.endlesscode.bukkitgradle.extensions.warnSyntaxChanged

// TODO 1.0: Remove deprecated methods
public class PluginConfigurationImpl(objects: ObjectFactory) : PluginConfiguration {

    private val _generatePluginYaml: Property<Boolean> = objects.property<Boolean>()
        .convention(true)
        .finalizedOnRead()
    override val generatePluginYaml: Property<Boolean> = _generatePluginYaml

    override val name: Property<String> = objects.property()
    override val description: Property<String> = objects.property()
    override val main: Property<String> = objects.property()
    override val version: Property<String> = objects.property()
    override val apiVersion: Property<String> = objects.property()
    override val url: Property<String> = objects.property()
    override val authors: ListProperty<String> = objects.listProperty()

    private val logger = LoggerFactory.getLogger("PluginConfiguration")

    @Deprecated("Use property syntax instead", ReplaceWith("this.name.set(name)"))
    public fun setName(name: String) {
        logger.warnSyntaxChanged("bukkit.plugin.name = '...'", "bukkit.plugin.name.set('...')")
        this.name.set(name)
    }

    @Deprecated("Use property syntax instead", ReplaceWith("this.description.set(description)"))
    public fun setDescription(description: String) {
        logger.warnSyntaxChanged("bukkit.plugin.description = '...'", "bukkit.plugin.description.set('...')")
        this.description.set(description)
    }

    @Deprecated("Use property syntax instead", ReplaceWith("this.main.set(main)"))
    public fun setMain(main: String) {
        logger.warnSyntaxChanged("bukkit.plugin.main = '...'", "bukkit.plugin.main.set('...')")
        this.main.set(main)
    }

    @Deprecated("Use property syntax instead", ReplaceWith("this.version.set(version)"))
    public fun setVersion(version: String) {
        logger.warnSyntaxChanged("bukkit.plugin.version = '...'", "bukkit.plugin.version.set('...')")
        this.version.set(version)
    }

    @Deprecated("Use property syntax instead", ReplaceWith("this.url.set(url)"))
    public fun setUrl(url: String) {
        logger.warnSyntaxChanged("bukkit.plugin.url = '...'", "bukkit.plugin.url.set('...')")
        this.url.set(url)
    }

    @Deprecated("Use property syntax instead", ReplaceWith("this.authors.set(authors)"))
    public fun setAuthors(authors: List<String>) {
        logger.warnSyntaxChanged("bukkit.plugin.authors = [...]", "bukkit.plugin.authors.set([...])")
        this.authors.set(authors)
    }

    /** Disables plugin.yaml parsing and generation. */
    public fun disablePluginYamlGeneration() {
        _generatePluginYaml.set(false)
    }
}
