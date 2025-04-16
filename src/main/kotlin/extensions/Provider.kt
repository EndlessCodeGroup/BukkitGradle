package ru.endlesscode.bukkitgradle.extensions

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.io.File
import java.util.*

internal fun <T> Property<T>.finalizedOnRead(): Property<T> = apply { finalizeValueOnRead() }

// See: https://github.com/gradle/gradle/issues/12388
@Suppress("UnstableApiUsage")
internal fun <T : Any, R : Any> Provider<T>.mapNotNull(transform: (T) -> R?): Provider<R> {
    return map { value: T ->
        val transformedValue = transform(value)
        Optional.ofNullable(transformedValue)
    }
        .filter(Optional<R>::isPresent)
        .map(Optional<R>::get)
}

internal fun Project.directoryProvider(file: () -> File?): Provider<Directory> = layout.dir(provider(file))
