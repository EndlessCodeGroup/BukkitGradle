package ru.endlesscode.bukkitgradle.meta.task

import org.junit.Test
import ru.endlesscode.bukkitgradle.PluginTestBase

class GenerateMetaTest extends PluginTestBase {

    @Test
    void testCanAddTaskToProject() throws Exception {
        def task = project.tasks.getByName("generatePluginMeta")
        assert task instanceof GenerateMeta
    }
}
