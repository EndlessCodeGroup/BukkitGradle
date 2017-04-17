package ru.endlesscode.bukkitgradle.bukkit.meta

import org.junit.Test
import ru.endlesscode.bukkitgradle.bukkit.TestBase
import ru.endlesscode.bukkitgradle.meta.GenerateMeta

import static org.junit.Assert.assertTrue

class GenerateMetaTest extends TestBase {
    @Test
    void testCanAddTaskToProject() throws Exception {
        def task = project.tasks.getByName("generatePluginMeta")
        assertTrue(task instanceof GenerateMeta)
    }
}