package ru.endlesscode.bukkitgradle.meta.util

class StringUtils {

    private StringUtils() {
        // util class
    }

    /**
     * Resolves an object to String.
     * @param obj The object to resolve
     * @return Resolved string
     */
    static String resolve(def obj) {
        if (obj == null) {
            return null
        }

        if (obj instanceof String) {
            return obj
        }

        if (obj instanceof Closure) {
            return resolve(obj.call())
        }

        if (obj instanceof Class) {
            return obj.name
        }

        return obj.toString()
    }

    static String toCamelCase(String text) {
        def camelCaseText = text.replaceAll(/[ _-]([A-Za-z0-9])/) { _, letter -> letter.toUpperCase() }
        return camelCaseText.capitalize()
    }
}
