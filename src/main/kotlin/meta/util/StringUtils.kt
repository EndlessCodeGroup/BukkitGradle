package ru.endlesscode.bukkitgradle.meta.util

import groovy.lang.Closure

internal object StringUtils {

    /** Resolves String from the given [obj]. */
    @JvmStatic
    fun resolve(obj: Any?): String? {
        return when (obj) {
            null -> null
            is String -> obj
            is Closure<*> -> resolve(obj.call())
            is Class<*> -> obj.name
            else -> obj.toString()
        }
    }

    /** Converts [text] to PascalCase. */
    @JvmStatic
    fun toPascalCase(text: String): String {
        val camelCaseText = text.replace(Regex("[ _-]([A-Za-z0-9])")) { match -> match.groupValues[1].toUpperCase() }
        return camelCaseText.capitalize()
    }
}
