package ru.endlesscode.gradle.bukkit

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention

class BukkitPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.with {
            plugins.apply('java')
            plugins.apply('eclipse')
            plugins.apply('idea')

            convention.getPlugin(JavaPluginConvention).with {
                sourceCompatibility = targetCompatibility = JavaVersion.VERSION_1_8
            }

            repositories {
                mavenLocal()
                mavenCentral()

                maven {
                    name = 'spigot'
                    url = 'https://hub.spigotmc.org/nexus/content/repositories/snapshots/'
                }
            }
        }
    }
}
