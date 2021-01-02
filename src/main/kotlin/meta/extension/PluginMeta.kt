package ru.endlesscode.bukkitgradle.meta.extension

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property

// TODO 1.0: Remove deprecated methods
public class PluginMeta(objects: ObjectFactory) {

    @Input
    public val name: Property<String> = objects.property()

    @Optional
    @Input
    public val description: Property<String> = objects.property()

    @Input
    public val main: Property<String> = objects.property()

    @Input
    public val version: Property<String> = objects.property()

    @Optional
    @Input
    public val url: Property<String> = objects.property()

    @Optional
    @Input
    public val authors: ListProperty<String> = objects.listProperty()

    @get:Internal
    internal val items: Map<String, Provider<*>> = mapOf(
        "name" to name,
        "description" to description,
        "main" to main,
        "version" to version,
        "website" to url,
        "authors" to authors
    )

    @Deprecated("Use property syntax instead", ReplaceWith("this.name.set(name)"))
    public fun setName(name: String) {
        this.name.set(name)
    }

    @Deprecated("Use property syntax instead", ReplaceWith("this.description.set(description)"))
    public fun setDescription(description: String) {
        this.description.set(description)
    }

    @Deprecated("Use property syntax instead", ReplaceWith("this.main.set(main)"))
    public fun setMain(main: String) {
        this.main.set(main)
    }

    @Deprecated("Use property syntax instead", ReplaceWith("this.version.set(version)"))
    public fun setVersion(version: String) {
        this.version.set(version)
    }

    @Deprecated("Use property syntax instead", ReplaceWith("this.url.set(url)"))
    public fun setUrl(url: String) {
        this.url.set(url)
    }

    @Deprecated("Use property syntax instead", ReplaceWith("this.authors.set(authors)"))
    public fun setAuthors(authors: List<String>) {
        this.authors.set(authors)
    }

    /** Enclose [value] in single quotes. */
    public fun q(value: String): String = "'$value'"

    /** Enclose [value] in double quotes. */
    public fun qq(value: String): String = "\"$value\""
}
