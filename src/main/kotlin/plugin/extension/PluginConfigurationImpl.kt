package ru.endlesscode.bukkitgradle.plugin.extension

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import ru.endlesscode.bukkitgradle.extensions.finalizedOnRead

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

    /** Disables plugin.yaml parsing and generation. */
    public fun disablePluginYamlGeneration() {
        _generatePluginYaml.set(false)
    }
}
