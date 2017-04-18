package ru.endlesscode.bukkitgradle.server

import org.gradle.api.DefaultTask
import org.junit.Test
import ru.endlesscode.bukkitgradle.TestBase

import static org.junit.Assert.assertTrue

class ServerCoreTest extends TestBase {
    @Test
    void canAddTaskToProject() throws Exception {
        assertTrue(project.downloadServerCore instanceof DefaultTask)
    }

    @Test
    void testAllTasks() throws Exception {
        project.prepareServerCore.execute()
    }
}