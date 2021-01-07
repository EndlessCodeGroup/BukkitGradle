package ru.endlesscode.bukkitgradle.meta.extension

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import org.slf4j.LoggerFactory
import ru.endlesscode.bukkitgradle.extensions.warnSyntaxChanged

// TODO 1.0: Remove deprecated methods
public class PluginMetaImpl(objects: ObjectFactory) : PluginMeta {

    override val name: Property<String> = objects.property()
    override val description: Property<String> = objects.property()
    override val main: Property<String> = objects.property()
    override val version: Property<String> = objects.property()
    override val apiVersion: Property<String> = objects.property()
    override val url: Property<String> = objects.property()
    override val authors: ListProperty<String> = objects.listProperty()

    private val logger = LoggerFactory.getLogger("PluginMeta")

    @Deprecated("Use property syntax instead", ReplaceWith("this.name.set(name)"))
    public fun setName(name: String) {
        logger.warnSyntaxChanged("bukkit.meta.name = '...'", "bukkit.meta.name.set('...')")
        this.name.set(name)
    }

    @Deprecated("Use property syntax instead", ReplaceWith("this.description.set(description)"))
    public fun setDescription(description: String) {
        logger.warnSyntaxChanged("bukkit.meta.description = '...'", "bukkit.meta.description.set('...')")
        this.description.set(description)
    }

    @Deprecated("Use property syntax instead", ReplaceWith("this.main.set(main)"))
    public fun setMain(main: String) {
        logger.warnSyntaxChanged("bukkit.meta.main = '...'", "bukkit.meta.main.set('...')")
        this.main.set(main)
    }

    @Deprecated("Use property syntax instead", ReplaceWith("this.version.set(version)"))
    public fun setVersion(version: String) {
        logger.warnSyntaxChanged("bukkit.meta.version = '...'", "bukkit.meta.version.set('...')")
        this.version.set(version)
    }

    @Deprecated("Use property syntax instead", ReplaceWith("this.url.set(url)"))
    public fun setUrl(url: String) {
        logger.warnSyntaxChanged("bukkit.meta.url = '...'", "bukkit.meta.url.set('...')")
        this.url.set(url)
    }

    @Deprecated("Use property syntax instead", ReplaceWith("this.authors.set(authors)"))
    public fun setAuthors(authors: List<String>) {
        logger.warnSyntaxChanged("bukkit.meta.authors = [...]", "bukkit.meta.authors.set([...])")
        this.authors.set(authors)
    }

    /** Enclose [value] in single quotes. */
    public fun q(value: String): String = "'$value'"

    /** Enclose [value] in double quotes. */
    public fun qq(value: String): String = "\"$value\""
}
