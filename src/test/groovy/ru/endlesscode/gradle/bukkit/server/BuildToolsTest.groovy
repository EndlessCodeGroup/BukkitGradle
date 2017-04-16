package ru.endlesscode.gradle.bukkit.server

import de.undercouch.gradle.tasks.download.Download
import org.junit.Test
import ru.endlesscode.gradle.bukkit.TestBase

import java.nio.file.Files
import java.nio.file.Path

import static org.junit.Assert.assertTrue

class BuildToolsTest extends TestBase {
    @Test
    void canAddTaskToProject() throws Exception {
        def task = project.tasks.getByName("downloadBuildTools")
        assertTrue(task instanceof Download)
    }

    @Test
    void downloadingBuildToolsMustBeSuccessful() throws Exception {
        def task = project.tasks.getByName("downloadBuildTools")
        task.execute()

        Path buildTools = project.buildDir.toPath().resolve("server/BuildTools.jar")
        Files.exists(buildTools)
    }
}