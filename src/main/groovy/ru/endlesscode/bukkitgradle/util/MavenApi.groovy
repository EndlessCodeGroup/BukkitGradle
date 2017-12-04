package ru.endlesscode.bukkitgradle.util

import org.gradle.api.Project

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class MavenApi {

    private static Project project
    private static Path mavenLocal

    private MavenApi() {}

    static init(Project project) {
        this.project = project
        mavenLocal = Paths.get(project.repositories.mavenLocal().url)
    }

    static def hasSpigot(String version) {
        def groupId = 'org.spigotmc'
        return hasArtifact(groupId, 'spigot-api', version) &&
                hasArtifact(groupId, 'spigot', version)
    }

    static def hasArtifact(groupId, artifactId, version) {
        def artifactDir = mavenLocal.resolve("${groupId.replace('.', '/')}/$artifactId/$version/")
        return Files.exists(artifactDir)
    }
}
