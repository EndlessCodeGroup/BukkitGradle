package ru.endlesscode.bukkitgradle.server.extension

import spock.lang.Specification

class ServerConfigurationSpec extends Specification {

    // SUT
    ServerConfigurationImpl serverConfiguration

    void setup() {
        serverConfiguration = new ServerConfigurationImpl()
    }

    void 'when build args - should return args with eula flag'() {
        when:
        serverConfiguration.eula = true
        def args = serverConfiguration.buildJvmArgs()

        then:
        ["-Dcom.mojang.eula.agree=true", "-Xmx1G"] == args
    }

    void 'when set existing core - should set core successfully'() {
        when:
        serverConfiguration.core = "paper"

        then:
        CoreType.PAPER == serverConfiguration.coreType
    }

    void 'when set existing core in mixed case - should set core successfully'() {
        when:
        serverConfiguration.core = "Paper"

        then:
        CoreType.PAPER == serverConfiguration.coreType
    }

    void 'when set not existing core - should fallback to spigot core'() {
        when:
        serverConfiguration.core = "uber-bukkit"

        then:
        CoreType.SPIGOT == serverConfiguration.coreType
    }
}
