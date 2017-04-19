package ru.endlesscode.bukkitgradle.server

import org.gradle.api.DefaultTask
import org.junit.Test
import ru.endlesscode.bukkitgradle.TestBase

import static org.junit.Assert.assertTrue

class ServerCoreTest extends TestBase {
    @Test
    void canAddTasksToProject() throws Exception {
        assertTrue(project.updateServerCoreMetadata instanceof DefaultTask)
        assertTrue(project.downloadServerCore instanceof DefaultTask)
        assertTrue(project.copyServerCore instanceof DefaultTask)
    }
}