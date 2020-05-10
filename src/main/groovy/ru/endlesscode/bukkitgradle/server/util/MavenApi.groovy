package ru.endlesscode.bukkitgradle.server.util

import org.gradle.api.Project

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class MavenApi {

    private static Project project
    private static Path mavenLocal

    private MavenApi() {}

    static void init(Project project) {
        this.project = project
        mavenLocal = Paths.get(project.repositories.mavenLocal().url)
    }

    static boolean hasSpigot(String version) {
        def groupId = 'org.spigotmc'
        return hasArtifact(groupId, 'spigot-api', version) &&
                hasArtifact(groupId, 'spigot', version)
    }

    static boolean hasArtifact(String groupId, String artifactId, String version) {
        def artifactDir = getArtifactDir(groupId, artifactId, version)
        return Files.exists(artifactDir)
    }

    static Path getSpigotDir(String version) {
        return getArtifactDir('org.spigotmc', 'spigot', version)
    }

    static Path getArtifactDir(String groupId, String artifactId, String version) {
        return mavenLocal.resolve("${groupId.replace('.', '/')}/$artifactId/$version/")
    }
}
