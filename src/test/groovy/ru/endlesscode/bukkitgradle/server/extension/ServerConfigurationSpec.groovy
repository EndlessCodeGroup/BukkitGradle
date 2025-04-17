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
}
