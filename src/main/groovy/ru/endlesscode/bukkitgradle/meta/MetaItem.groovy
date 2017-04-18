package ru.endlesscode.bukkitgradle.meta

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

    String getValue() {
        return resolve(this.value)
    }

    /**
     * Converts and returns meta item to YAML format
     *
     * @return Converted item
     */
    String getEntry() {
        return "$id: ${getValue()}"
    }

    /**
     * Validates meta item
     *
     * @return true if meta item valid, otherwise false
     */
    boolean isValid() {
        return !this.required || getValue() != null
    }

    /**
     * Resolves all objects to String
     *
     * @param obj The object to resolve
     * @return Resolved string
     */
    private static String resolve(def obj) {
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
