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

    @Internal
    internal val items: Map<String, Provider<*>> = mapOf(
        "name" to name,
        "description" to description,
        "main" to main,
        "version" to version,
        "website" to url,
        "authors" to authors
    )

    /** Enclose [value] in single quotes. */
    public fun q(value: String): String = "'$value'"

    /** Enclose [value] in double quotes. */
    public fun qq(value: String): String = "\"$value\""
}
