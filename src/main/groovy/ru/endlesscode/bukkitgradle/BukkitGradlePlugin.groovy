package ru.endlesscode.bukkitgradle

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.compile.JavaCompile
import ru.endlesscode.bukkitgradle.meta.PluginMetaPlugin
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta
import ru.endlesscode.bukkitgradle.meta.util.StringUtils
import ru.endlesscode.bukkitgradle.server.extension.RunConfiguration
import ru.endlesscode.bukkitgradle.server.legacy.LegacyDevServerPlugin

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
            extensions.create("bukkit", BukkitGroovy, configurePluginMeta(), new RunConfiguration())

            plugins.with {
                apply('java')
                apply(PluginMetaPlugin)
                apply(LegacyDevServerPlugin)
            }

            convention.getPlugin(JavaPluginConvention).with {
                sourceCompatibility = targetCompatibility = JavaVersion.VERSION_1_8
            }
        }
    }

    private PluginMeta configurePluginMeta() {
        return new PluginMeta(project.objects).tap {
            name.convention(project.name)
            description.convention(project.provider { project.description })
            main.convention(name.map { "${project.group}.${StringUtils.toPascalCase(it)}" })
            version.convention(project.provider { project.version.toString() })
            url.convention(project.provider { project.findProperty("url")?.toString() })
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
