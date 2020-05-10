package ru.endlesscode.bukkitgradle.extension

import org.junit.Before
import org.junit.Test
import ru.endlesscode.bukkitgradle.server.CoreType

class RunConfigurationTest {

    // SUT
    RunConfiguration runConfiguration

    @Before
    void setUp() {
        runConfiguration = new RunConfiguration()
    }

    @Test
    void 'when build args - should return args with debug flags'() {
        // When
        def args = runConfiguration.buildJvmArgs()

        // Then
        assert "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dfile.encoding=UTF-8 -Xmx1G" == args
    }

    @Test
    void 'when build args - and debug disabled - should return args without debug flags'() {
        // Given
        runConfiguration.debug = false

        // When
        def args = runConfiguration.buildJvmArgs()

        // Then
        assert "-Dfile.encoding=UTF-8 -Xmx1G" == args
    }

    @Test
    void 'when build args with debug mode override - should return args without debug flags'() {
        // When
        def args = runConfiguration.buildJvmArgs(false)

        // Then
        assert "-Dfile.encoding=UTF-8 -Xmx1G" == args
    }

    @Test
    void 'when set existing core - should set core successfully'() {
        // When
        runConfiguration.core = "paper"

        // Then
        assert CoreType.PAPER == runConfiguration.coreType
    }

    @Test
    void 'when set existing core in mixed case - should set core successfully'() {
        // When
        runConfiguration.core = "Paper"

        // Then
        assert CoreType.PAPER == runConfiguration.coreType
    }

    @Test
    void 'when set not existing core - should fallback to spigot core'() {
        // When
        runConfiguration.core = "uber-bukkit"

        // Then
        assert CoreType.SPIGOT == runConfiguration.coreType
    }
}
