package ru.endlesscode.bukkitgradle.meta.extension

import ru.endlesscode.bukkitgradle.meta.util.StringUtils

class MetaItem {
    final String id

    def value

    MetaItem(String id, def value = null) {
        this.id = id
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
}
