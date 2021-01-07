package ru.endlesscode.bukkitgradle.server.extension

import spock.lang.Specification

class ServerConfigurationSpec extends Specification {

    // SUT
    ServerConfigurationImpl serverConfiguration

    void setup() {
        serverConfiguration = new ServerConfigurationImpl()
    }

    void 'when build args - should return args with debug flags'() {
        when:
        def args = serverConfiguration.buildJvmArgs(true)

        then:
        ["-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005", "-Dfile.encoding=UTF-8", "-Xmx1G"] == args
    }

    void 'when build args - and debug disabled - should return args without debug flags'() {
        when:
        def args = serverConfiguration.buildJvmArgs(false)

        then:
        ["-Dfile.encoding=UTF-8", "-Xmx1G"] == args
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
