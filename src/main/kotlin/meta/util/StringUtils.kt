package ru.endlesscode.bukkitgradle.meta.util

internal object StringUtils {

    /** Converts [text] to PascalCase. */
    @JvmStatic
    fun toPascalCase(text: String): String {
        val camelCaseText = text.replace(Regex("[ _-]([A-Za-z0-9])")) { match -> match.groupValues[1].toUpperCase() }
        return camelCaseText.capitalize()
    }

    @JvmStatic
    fun parseApiVersion(version: String): String? {
        val versionParts = version.split('.').mapNotNull { it.toIntOrNull() }
        require(versionParts.size in 2..3) { "Unable to parse API version '$version'." }
        val (major, minor) = versionParts
        return if (major >= 1 && minor >= 13) "$major.$minor" else null
    }
}
