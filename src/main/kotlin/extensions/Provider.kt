package ru.endlesscode.bukkitgradle.extensions

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.io.File

internal fun <T> Property<T>.finalizedOnRead(): Property<T> = apply { finalizeValueOnRead() }

internal fun Project.directoryProvider(file: () -> File?): Provider<Directory> = layout.dir(provider(file))
