@file:UseSerializers(PermissionDefaultSerializer::class)

package ru.endlesscode.bukkitgradle.plugin

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml
import xyz.jpenilla.resourcefactory.bukkit.Permission.Default as PermissionDefault

/**
 * Should be synced with [BukkitPluginYaml.Serializable].
 * We can't use the existing class as we want all fields to be nullable.
 */
@Serializable
internal data class BukkitPluginYamlDefaults(
    val apiVersion: String? = null,
    val name: String? = null,
    val version: String? = null,
    val main: String? = null,
    val description: String? = null,
    val load: BukkitPluginYaml.PluginLoadOrder? = null,
    val author: String? = null,
    val authors: List<String>? = null,
    val website: String? = null,
    val depend: List<String>? = null,
    val softdepend: List<String>? = null,
    val loadbefore: List<String>? = null,
    val prefix: String? = null,
    val defaultPermission: PermissionDefault? = null,
    val provides: List<String>? = null,
    val libraries: List<String>? = null,
    val commands: Map<String, Command>? = null,
    val permissions: Map<String, Permission>? = null,
    val foliaSupported: Boolean? = null,
    val paperPluginLoader: String? = null,
    val paperSkipLibraries: Boolean? = null,
) {

    @Serializable
    internal data class Command(
        val description: String? = null,
        val aliases: List<String>? = null,
        val permission: String? = null,
        val permissionMessage: String? = null,
        val usage: String? = null,
    )

    @Serializable
    internal data class Permission(
        val description: String? = null,
        val default: PermissionDefault? = null,
        val children: Map<String, Boolean>? = null,
    )
}

internal object PermissionDefaultSerializer : KSerializer<PermissionDefault> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "xyz.jpenilla.resourcefactory.bukkit.Permission.Default",
        PrimitiveKind.STRING,
    )

    override fun deserialize(decoder: Decoder): PermissionDefault {
        val string = decoder.decodeString()
        return PermissionDefault.values().first { it.serialized.equals(string, ignoreCase = true) }
    }

    override fun serialize(encoder: Encoder, value: PermissionDefault) {
        encoder.encodeString(value.serialized)
    }
}
