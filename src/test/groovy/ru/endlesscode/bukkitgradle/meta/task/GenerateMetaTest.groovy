package ru.endlesscode.bukkitgradle.meta.task

import org.junit.Test
import ru.endlesscode.bukkitgradle.PluginTestBase

class GenerateMetaTest extends PluginTestBase {

    @Test
    void 'when project initialize - should add generate plugin meta task'() throws Exception {
        // When
        def task = project.tasks.generatePluginMeta

        // Then
        assert task instanceof GenerateMeta
    }
}
