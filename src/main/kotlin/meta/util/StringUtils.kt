package ru.endlesscode.bukkitgradle.meta.util

internal object StringUtils {

    /** Converts [text] to PascalCase. */
    @JvmStatic
    fun toPascalCase(text: String): String {
        val camelCaseText = text.replace(Regex("[ _-]([A-Za-z0-9])")) { match -> match.groupValues[1].toUpperCase() }
        return camelCaseText.capitalize()
    }
}
