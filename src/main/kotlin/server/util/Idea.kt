package ru.endlesscode.bukkitgradle.server.util

import org.gradle.api.file.Directory
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import java.io.File
import javax.inject.Inject

internal abstract class Idea @Inject constructor(
    providers: ProviderFactory,
    private val layout: ProjectLayout,
) {

    val isActive: Provider<Boolean> = providers.systemProperty(IDEA_ACTIVE)
            .map { it.toBoolean() }
            .orElse(false)

    /** Directory containing '.idea/' */
    @Suppress("UnstableApiUsage")
    val projectDir: Provider<Directory> =
        layout.dir(providers.provider { findProjectDir(layout.settingsDirectory.asFile) })

    private tailrec fun findProjectDir(dir: File): File? {
        if (dir.resolve(".idea").isDirectory) return dir
        return findProjectDir(dir.parentFile ?: return null)
    }

    fun relativePathOf(file: File): Provider<String> = projectDir.map {
        val relativePath = file.toRelativeString(it.asFile)
        "\$PROJECT_DIR\$/$relativePath".trimEnd('/')
    }

    fun runConfigurationFile(dir: Directory, name: String): RegularFile {
        val extension = if (dir.asFile.endsWith(".idea/runConfiguration")) ".xml" else ".run.xml"
        return dir.file("${sanitizeFileName(name)}$extension")
    }

    companion object {
        private const val IDEA_ACTIVE: String = "idea.active"

        private val charsToReplace = Regex("""[<>:\\"|?*]""")

        @JvmStatic
        fun sanitizeFileName(name: String): String = name.replace(charsToReplace, "_")
    }
}
