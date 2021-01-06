package ru.endlesscode.bukkitgradle

class BukkitGradlePluginSpec extends PluginSpecification {

    def setup() {
        project.apply(plugin: BukkitGradlePlugin)
    }

    def "when initialized - should add required plugins"() {
        expect: "java plugin added"
        project.pluginManager.hasPlugin("java")
    }

    def "when use custom repos extension - should add repos"() {
        when: "use repo extension"
        project.repositories.sk89q()

        then: "repository is added"
        project.repositories.findByName("sk89q") != null
    }

    def "when use bukkit extension - and bukkit version not set - should return bukkit dependency with default version"() {
        when: "use bukkit extension"
        String dependency = project.dependencies.bukkit()

        then: "returned bukkit dependency with default version"
        dependency == 'org.bukkit:bukkit:1.16.4-R0.1-SNAPSHOT'
    }

    def "when use bukkit extension - and bukkit version set - should return bukkit with specified version"() {
        given: "api version specified"
        project.bukkit.apiVersion = "1.7.10"

        when: "use bukkit extension"
        String dependency = project.dependencies.bukkit()

        then: "returned bukkit dependency with the specified version"
        dependency == 'org.bukkit:bukkit:1.7.10-R0.1-SNAPSHOT'
    }

    def "when use bukkit extension - should add required repo"() {
        when: "use bukkit dependency"
        project.dependencies.bukkit()

        then: "required repository is applied"
        project.repositories.findByName("Spigot") != null
    }

    def "when use spigot extension - should add repo mavenLocal"() {
        given: "mavenLocal is not applied"
        project.repositories.findByName("MavenLocal") == null

        when: "use spigot extension"
        project.dependencies.spigot()

        then: "mavenLocal applied"
        project.repositories.findByName("MavenLocal") != null
    }
}
