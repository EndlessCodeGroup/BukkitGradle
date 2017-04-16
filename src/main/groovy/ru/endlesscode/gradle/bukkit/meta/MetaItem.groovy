package ru.endlesscode.gradle.bukkit.meta

class MetaItem {
    final String id
    final boolean required

    def value

    MetaItem(String id, def value, boolean required = false) {
        this(id, required, value)
    }

    MetaItem(String id, boolean required = false, def value = null) {
        this.id = id
        this.required = required
        this.value = value
    }

    String getEntry() {
        return "$id: ${getValue()}"
    }

    String getValue() {
        return resolve(this.value)
    }

    private static String resolve(Object obj) {
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

        return obj as String
    }
}
