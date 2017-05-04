package ru.endlesscode.bukkitgradle.meta

import org.junit.Test
import ru.endlesscode.bukkitgradle.TestBase
import ru.endlesscode.bukkitgradle.task.GenerateMeta

import static org.junit.Assert.assertTrue

class GenerateMetaTest extends TestBase {
    @Test
    void testCanAddTaskToProject() throws Exception {
        def task = project.tasks.getByName("generatePluginMeta")
        assertTrue(task instanceof GenerateMeta)
    }
}