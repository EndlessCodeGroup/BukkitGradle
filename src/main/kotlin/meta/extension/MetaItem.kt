package ru.endlesscode.bukkitgradle.meta.extension

import ru.endlesscode.bukkitgradle.meta.util.StringUtils

internal class MetaItem @JvmOverloads constructor(val id: String, value: Any? = null) {

    private var _value: Any? = value

    fun getValue(): String? = StringUtils.resolve(_value)
    fun setValue(value: Any?) {
        _value = value
    }

    /**
     * Converts and returns meta item to YAML format
     *
     * @return Converted item
     */
    fun getEntry(): String = "$id: ${getValue()}"
}
