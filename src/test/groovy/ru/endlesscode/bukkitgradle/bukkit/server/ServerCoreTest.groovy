package ru.endlesscode.bukkitgradle.bukkit.server

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.junit.Test
import ru.endlesscode.bukkitgradle.bukkit.TestBase

import static org.junit.Assert.assertTrue

class ServerCoreTest extends TestBase {
    @Test
    void canAddTaskToProject() throws Exception {
        assertTrue(project.downloadServerCore instanceof DefaultTask)
    }

    @Test
    void downloadingServerCoreMustBeSuccessful() throws Exception {
        executeTask(project.downloadServerCore as Task)
    }
}