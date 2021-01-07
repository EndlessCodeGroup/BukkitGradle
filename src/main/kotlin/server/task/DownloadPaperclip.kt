package ru.endlesscode.bukkitgradle.server.task

import de.undercouch.gradle.tasks.download.Download
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.StopExecutionException
import org.gradle.kotlin.dsl.property
import ru.endlesscode.bukkitgradle.TASKS_GROUP_BUKKIT
import java.io.File
import javax.inject.Inject

public open class DownloadPaperclip @Inject constructor(objects: ObjectFactory) : Download() {

    @InputFile
    public val paperVersionsFile: Property<File> = objects.property()

    @Input
    public val version: Property<String> = objects.property()

    @OutputFile
    public val paperclipFile: Provider<File> = project.provider { outputFiles.single() }

    init {
        group = TASKS_GROUP_BUKKIT
        description = "Download paperclip"

        src(paperVersionsFile.zip(version, ::extractPaperUrl))
        onlyIfModified(true)
    }

    private fun extractPaperUrl(versionsFile: File, version: String): String {
        if (!versionsFile.isFile) {
            project.logger.warn("Paper versions file not downloaded, make sure that Gradle isn\'t running in offline mode.")
            throw StopExecutionException()
        }

        val versionsUrls = Json.decodeFromString<PaperVersions>(versionsFile.readText()).versions
        val paperUrl = versionsUrls[version]
        if (paperUrl == null) {
            project.logger.warn(
                """
                Paper v$version not found.
                Supported paper versions: ${versionsUrls.keys}.
                """.trimIndent()
            )
            throw StopExecutionException()
        }

        return paperUrl
    }

    @Serializable
    private data class PaperVersions(
        val latest: String,
        val versions: Map<String, String>
    )
}
