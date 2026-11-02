//file:noinspection ConfigurationAvoidance
package ru.endlesscode.bukkitgradle

class BukkitGradlePluginSpec extends PluginSpecification {

    def setup() {
        project.apply(plugin: BukkitGradlePlugin)
    }

    def "when initialized - and apiVersion is not set - should show an error"() {
        when: "use some API requiring apiVersion to be set"
        project.java.toolchain.languageVersion.get()

        then:
        def exception = thrown(RuntimeException)
        exception.cause.message == "Please, set 'bukkit.apiVersion' property."
    }

    def "when initialized - should set default JVM toolchain"(String apiVersion, int jvmVersion) {
        given: "apiVersion is set"
        project.bukkit.apiVersion = apiVersion

        expect: "JVM toolchain version should be"
        project.java.toolchain.languageVersion.get().asInt() == jvmVersion

        where:
        apiVersion | jvmVersion
        "1.16.5"   | 8
        "1.17"     | 16
        "1.17.1"   | 16
        "1.18"     | 17
        "1.20.4"   | 17
        "1.20.5"   | 21
        "26.1"     | 25
        "26.1.2"   | 25
        "26.3"     | 25
    }

    def "when use custom repos extension - should add repos"() {
        when: "use repo extension"
        project.repositories.sk89q()

        then: "repository is added"
        project.repositories.findByName("sk89q") != null
    }

    def "when use bukkit extension - should return bukkit dependency with version placeholder"() {
        when:
        String dependency = project.dependencies.bukkitApi

        then:
        dependency == 'org.bukkit:bukkit:{bukkit.apiVersion}'
    }

    def "when using paper extension - should add paper dependency"(String apiVersion, String expectedDependency) {
        given:
        project.bukkit.apiVersion = apiVersion

        when: "use paperApi extension"
        project.dependencies {
            compileOnly(paperApi) { transitive = false }
        }

        project.repositories {
            papermc()
        }

        then: "resolved paper dependency with correct groupId"
        resolvedDependency().startsWith(expectedDependency)

        where:
        apiVersion | expectedDependency
        "1.16.5"   | "com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT"
        "1.18"     | "io.papermc.paper:paper-api:1.18-R0.1-SNAPSHOT"
        "1.21.11"  | "io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT"
        "26.1"     | "io.papermc.paper:paper-api:26.1."
        "26.1.2"   | "io.papermc.paper:paper-api:26.1.2.build."
        "26.3"     | "io.papermc.paper:paper-api:26.3.build."
    }

    def "when using paper with non-release version - should keep the version"() {
        given:
        project.bukkit.apiVersion = "26.3"

        when:
        project.dependencies {
            compileOnly("io.papermc.paper:paper-api:26.3-pre-2.build.0-alpha") { transitive = false }
        }

        project.repositories {
            papermc()
        }

        then:
        resolvedDependency() == "io.papermc.paper:paper-api:26.3-pre-2.build.0-alpha"
    }

    def "when using paper with old group and new version - should fix the group"() {
        given:
        project.bukkit.apiVersion = "1.18"

        when:
        project.dependencies {
            compileOnly("com.destroystokyo.paper:paper-api:1.17-R0.1-SNAPSHOT")
        }

        project.repositories {
            papermc()
        }

        then: "resolved paper dependency with correct groupId"
        resolvedDependency().startsWith("io.papermc.paper:paper-api:1.17-R0.1-SNAPSHOT")
    }

    def "when use bukkit extension - and bukkit version set - should return bukkit with specified version"(String apiVersion) {
        given: "api version specified"
        project.bukkit.apiVersion = apiVersion

        when: "use bukkit extension"
        project.dependencies {
            compileOnly(spigotApi) { transitive = false }
        }

        project.repositories {
            spigot()
        }

        then: "resolved bukkit dependency with the specified version"
        resolvedDependency().startsWith("org.spigotmc:spigot-api:$apiVersion-R0.1-SNAPSHOT")

        where:
        apiVersion << ["1.20.5", "26.1"]
    }

    private String resolvedDependency() {
        def compileClasspath = project.configurations.getByName("compileClasspath")
        def artifacts = compileClasspath.incoming.artifacts.resolvedArtifacts.get()
        return artifacts.first().id.componentIdentifier.displayName
    }
}
