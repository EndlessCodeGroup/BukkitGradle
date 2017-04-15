package ru.endlesscode.gradle.bukkit

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.compile.JavaCompile
import ru.endlesscode.gradle.bukkit.meta.PluginMetaPlugin

class BukkitPlugin implements Plugin<Project> {
    Project project

    @Override
    void apply(Project project) {
        this.project = project

        configureProject()
    }

    def configureProject() {
        addPlugins()
        configureEncoding()
        addRepositories()
        addDependencies()
    }

    def addPlugins() {
        project.with {
            plugins.with {
                apply("java")
                apply("eclipse")
                apply("idea")
                apply(PluginMetaPlugin)
            }

            convention.getPlugin(JavaPluginConvention).with {
                sourceCompatibility = targetCompatibility = JavaVersion.VERSION_1_8
            }
        }
    }

    def configureEncoding() {
        project.tasks.withType(JavaCompile) {
            options.encoding = "UTF-8"
        }
    }

    def addRepositories() {
        project.with {
            repositories {
                mavenLocal()
                mavenCentral()

                maven {
                    name = "sk89q"
                    url = "http://maven.sk89q.com/repo/org/sk89q/"
                }

                maven {
                    name = "spigot"
                    url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/"
                }
            }
        }
    }

    def addDependencies() {
        project.gradle.addListener(new DependencyResolutionListener() {
            @Override
            void beforeResolve(ResolvableDependencies resolvableDependencies) {
                addBukkitApi(project)
                project.gradle.removeListener(this)
            }

            @Override
            void afterResolve(ResolvableDependencies resolvableDependencies) {}
        })
    }

    static def addBukkitApi(Project project) {
        project.with {
            def compileDeps = configurations.compile.dependencies
            compileDeps.add(dependencies.create("org.bukkit:bukkit:$bukkit.version"))
        }
    }
}
