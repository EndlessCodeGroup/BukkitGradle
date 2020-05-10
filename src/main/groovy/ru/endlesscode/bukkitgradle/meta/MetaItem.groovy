package ru.endlesscode.bukkitgradle.meta

import ru.endlesscode.bukkitgradle.util.StringUtils

class MetaItem {
    final String id
    final boolean required

    def value

    MetaItem(String id, boolean required = false, def value = null) {
        this.id = id
        this.required = required
        this.value = value
    }

    String getValue() {
        return StringUtils.resolve(this.value)
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
}
