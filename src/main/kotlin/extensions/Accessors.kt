package ru.endlesscode.bukkitgradle.extensions

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.the
import xyz.jpenilla.resourcefactory.ResourceFactoryExtension

internal val Project.java: JavaPluginExtension
    get() = the()

internal val Project.sourceSets: SourceSetContainer
    get() = the()

internal val SourceSet.resourceFactory: ResourceFactoryExtension
    get() = the()
