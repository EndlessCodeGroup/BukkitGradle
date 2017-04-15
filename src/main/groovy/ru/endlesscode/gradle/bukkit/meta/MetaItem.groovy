package ru.endlesscode.gradle.bukkit.meta

class MetaItem {
    final String id
    final boolean required

    def value

    MetaItem(String id) {
        this(id, false)
    }

    MetaItem(String id, boolean required) {
        this(id, required, null)
    }

    MetaItem(String id, def value) {
        this(id, false, value)
    }

    MetaItem(String id, boolean required, def value) {
        this.id = id
        this.required = required
        this.value = value
    }

    String getValue() {
        return PluginMeta.resolve(this.value)
    }

    String getEntry() {
        return "$id: ${getValue()}"
    }
}
