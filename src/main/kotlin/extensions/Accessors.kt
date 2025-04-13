package ru.endlesscode.bukkitgradle.extensions

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.the

internal val Project.java: JavaPluginExtension
    get() = the()
