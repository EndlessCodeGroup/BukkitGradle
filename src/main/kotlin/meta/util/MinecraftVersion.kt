package ru.endlesscode.bukkitgradle.meta.util

import ru.endlesscode.bukkitgradle.Bukkit

@JvmInline
internal value class MinecraftVersion(private val value: Int) : Comparable<MinecraftVersion> {
    private val major: Int get() = value / 1_00_00
    private val minor: Int get() = (value / 1_00) % 100
    private val patch: Int get() = value % 100

    fun withoutPatch(): MinecraftVersion = MinecraftVersion(value - patch)

    override fun toString(): String = if (patch == 0) "$major.$minor" else "$major.$minor.$patch"
    override fun compareTo(other: MinecraftVersion): Int = value.compareTo(other.value)

    companion object {
        fun parse(version: String): MinecraftVersion {
            val versionParts = version.split('.').mapNotNull { it.toIntOrNull() }
            require(versionParts.size in 2..3) { "Unable to parse API version '$version'." }
            val (major, minor, patch) = versionParts + 0
            return MinecraftVersion(major * 1_00_00 + minor * 1_00 + patch)
        }

        val V1_12_0 = MinecraftVersion(1_12_00)
        val V1_13_0 = MinecraftVersion(1_13_00)
        val V1_16_5 = MinecraftVersion(1_16_05)
        val V1_17_1 = MinecraftVersion(1_17_01)
        val V1_20_5 = MinecraftVersion(1_20_05)
    }
}

internal val Bukkit.parsedApiVersion get() = MinecraftVersion.parse(apiVersion)
