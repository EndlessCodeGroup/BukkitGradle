package ru.endlesscode.bukkitgradle

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import ru.endlesscode.bukkitgradle.meta.MetaFile

import java.nio.file.Files
import java.nio.file.Path

class PluginTestBase {

    protected Project project

    @Before
    void setUp() throws Exception {
        System.properties.setProperty("test", "true")

        project = ProjectBuilder.builder()
                .withName("TestProject")
                .withProjectDir(new File("build/testProject"))
                .build()

        project.with {
            apply(plugin: BukkitGradlePlugin)

            group = "com.example.plugin"
            description = "Test project description"
            version = "1.0"
            ext.url = "https://www.example.ru/"
        }
    }

    protected void initBukkitMeta(
            String name = "TestPlugin",
            String description = "Test plugin description",
            String version = "0.1",
            String main = "com.example.plugin.Plugin",
            List<String> authors = ["OsipXD", "Contributors"],
            String url = "http://www.example.com/"
    ) {
        project.bukkit.meta {
            delegate.name = name
            delegate.description = description
            delegate.version = version
            delegate.main = main
            delegate.authors = authors
            delegate.url = url
        }
    }

    protected void executeTask(Task task) {
        task.taskDependencies.getDependencies(task).each {
            subTask -> executeTask(subTask)
        }

        task.execute()
    }
}
