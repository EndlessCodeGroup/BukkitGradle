package ru.endlesscode.bukkitgradle

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.compile.JavaCompile
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta
import ru.endlesscode.bukkitgradle.server.extension.RunConfiguration

class BukkitGradlePlugin implements Plugin<Project> {
    final static String GROUP = 'Bukkit'

    Project project

    static boolean isTesting() {
        System.properties['test'] == 'true'
    }

    @Override
    void apply(Project project) {
        this.project = project
        configureProject()
    }

    /**
     * Configures project
     */
    private void configureProject() {
        addRepositories()
        addPlugins()
        configureEncoding()
        Dependencies.configureProject(project)
    }

    /**
     * Adds all needed plugins
     */
    private void addPlugins() {
        project.with {
            extensions.create(Bukkit.NAME, Bukkit, new PluginMeta(project), new RunConfiguration())

            plugins.with {
                apply('java')
                apply(PluginMetaPlugin)
                apply(DevServerPlugin)
            }

            convention.getPlugin(JavaPluginConvention).with {
                sourceCompatibility = targetCompatibility = JavaVersion.VERSION_1_8
            }
        }
    }

    /**
     * Sets force encoding on compile to UTF-8
     */
    private void configureEncoding() {
        project.tasks.withType(JavaCompile) {
            options.encoding = 'UTF-8'
        }
    }

    /**
     * Adds needed repositories
     */
    private void addRepositories() {
        project.repositories {
            mavenLocal()
            jcenter()
        }
    }
}
