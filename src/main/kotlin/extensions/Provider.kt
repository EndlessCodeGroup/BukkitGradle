package ru.endlesscode.bukkitgradle.extensions

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.util.*

internal fun <T> Property<T>.finalizeAndGet(): T {
    finalizeValue()
    return get()
}

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
