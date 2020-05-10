package ru.endlesscode.bukkitgradle.server

import org.gradle.api.DefaultTask
import org.junit.Ignore
import org.junit.Test
import ru.endlesscode.bukkitgradle.PluginTestBase

class ServerCoreTest extends PluginTestBase {

    @Ignore
    @Test
    void canAddTasksToProject() throws Exception {
        assert project.downloadBukkitMeta instanceof DefaultTask
        assert project.copyServerCore instanceof DefaultTask
    }
}
