package ru.endlesscode.bukkitgradle.server.extension

import spock.lang.Specification

class RunConfigurationSpec extends Specification {

    // SUT
    RunConfigurationImpl runConfiguration

    void setup() {
        runConfiguration = new RunConfigurationImpl()
    }

    void 'when build args - should return args with debug flags'() {
        when:
        def args = runConfiguration.buildJvmArgs()

        then:
        "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dfile.encoding=UTF-8 -Xmx1G" == args
    }

    void 'when build args - and debug disabled - should return args without debug flags'() {
        given:
        runConfiguration.debug = false

        when:
        def args = runConfiguration.buildJvmArgs()

        then:
        "-Dfile.encoding=UTF-8 -Xmx1G" == args
    }

    void 'when set existing core - should set core successfully'() {
        when:
        runConfiguration.core = "paper"

        then:
        CoreType.PAPER == runConfiguration.coreType
    }

    void 'when set existing core in mixed case - should set core successfully'() {
        when:
        runConfiguration.core = "Paper"

        then:
        CoreType.PAPER == runConfiguration.coreType
    }

    void 'when set not existing core - should fallback to spigot core'() {
        when:
        runConfiguration.core = "uber-bukkit"

        then:
        CoreType.SPIGOT == runConfiguration.coreType
    }
}
