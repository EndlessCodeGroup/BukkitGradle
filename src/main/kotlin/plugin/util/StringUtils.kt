package ru.endlesscode.bukkitgradle.plugin.util

internal object StringUtils {

    /** Converts [text] to PascalCase. */
    @JvmStatic
    fun toPascalCase(text: String): String {
        val camelCaseText = text.replace(Regex("[ _-]([A-Za-z0-9])")) { match -> match.groupValues[1].uppercase() }
        return camelCaseText.replaceFirstChar { it.uppercase() }
    }
}
