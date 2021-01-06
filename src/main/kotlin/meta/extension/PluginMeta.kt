package ru.endlesscode.bukkitgradle.meta.extension

import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

public interface PluginMeta {

    @get:Input
    public val name: Provider<String>

    @get:Optional
    @get:Input
    public val description: Provider<String>

    @get:Input
    public val main: Provider<String>

    @get:Input
    public val version: Provider<String>

    @get:Optional
    @get:Input
    public val url: Provider<String>

    @get:Optional
    @get:Input
    public val authors: Provider<List<String>>
}

internal val PluginMeta.items: Map<String, Provider<*>>
    get() = mapOf(
        "name" to name,
        "description" to description,
        "main" to main,
        "version" to version,
        "website" to url,
        "authors" to authors
    )
