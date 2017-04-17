package ru.endlesscode.bukkitgradle.bukkit.server

import de.undercouch.gradle.tasks.download.Download
import org.junit.Test
import ru.endlesscode.bukkitgradle.bukkit.TestBase

import static org.junit.Assert.assertTrue

class ServerCoreTest extends TestBase {
    @Test
    void canAddTaskToProject() throws Exception {
        assertTrue(project.downloadServerCore instanceof Download)
    }

    @Test
    void downloadingBuildToolsMustBeSuccessful() throws Exception {
        project.updateServerCoreMetadata.execute()
        project.downloadServerCore.execute()
    }
}