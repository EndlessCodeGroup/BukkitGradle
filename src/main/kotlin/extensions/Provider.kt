package ru.endlesscode.bukkitgradle.extensions

import org.gradle.api.provider.Property

internal fun <T> Property<T>.finalizedOnRead(): Property<T> = apply { finalizeValueOnRead() }
