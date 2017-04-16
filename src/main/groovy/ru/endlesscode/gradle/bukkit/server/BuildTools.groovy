package ru.endlesscode.gradle.bukkit.server

import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.Project

class BuildTools {
    BuildTools(Project project) {
        project.with {
            File buildToolsDir = new File(buildDir, "/server")
            buildToolsDir.mkdirs()

            task("downloadBuildTools", type: Download) {
                src 'https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar'
                dest buildToolsDir
            }
        }
    }
}
