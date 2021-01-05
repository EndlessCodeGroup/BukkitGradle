package ru.endlesscode.bukkitgradle.server.legacy.util

import org.gradle.api.Project

class MavenApi {

    private static File mavenLocal

    private MavenApi() {}

    static void init(Project project) {
        mavenLocal = new File(project.repositories.mavenLocal().url)
    }

    static boolean hasSpigot(String version) {
        def groupId = 'org.spigotmc'
        return hasArtifact(groupId, 'spigot-api', version) &&
                hasArtifact(groupId, 'spigot', version)
    }

    static boolean hasArtifact(String groupId, String artifactId, String version) {
        def artifactDir = getArtifactDir(groupId, artifactId, version)
        return artifactDir.exists()
    }

    static File getSpigotDir(String version) {
        return getArtifactDir('org.spigotmc', 'spigot', version)
    }

    static File getArtifactDir(String groupId, String artifactId, String version) {
        return new File(mavenLocal, "${groupId.replace('.', '/')}/$artifactId/$version/")
    }
}
