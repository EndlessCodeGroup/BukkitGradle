package ru.endlesscode.bukkitgradle.server

import org.gradle.api.DefaultTask
import org.junit.Test
import ru.endlesscode.bukkitgradle.TestBase

import static org.junit.Assert.assertTrue

class ServerCoreTest extends TestBase {
    @Test
    void canAddTasksToProject() throws Exception {
        assertTrue(project.downloadBukkitMeta instanceof DefaultTask)
        assertTrue(project.copyServerCore instanceof DefaultTask)
    }
}
