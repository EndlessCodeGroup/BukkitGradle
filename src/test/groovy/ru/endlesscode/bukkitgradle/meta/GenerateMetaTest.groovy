package ru.endlesscode.bukkitgradle.meta

import org.junit.Test
import ru.endlesscode.bukkitgradle.PluginTestBase
import ru.endlesscode.bukkitgradle.task.GenerateMeta

class GenerateMetaTest extends PluginTestBase {

    @Test
    void testCanAddTaskToProject() throws Exception {
        def task = project.tasks.getByName("generatePluginMeta")
        assert task instanceof GenerateMeta
    }
}
