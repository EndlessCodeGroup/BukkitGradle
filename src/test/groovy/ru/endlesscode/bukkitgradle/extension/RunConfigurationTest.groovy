package ru.endlesscode.bukkitgradle.extension

import org.junit.Test
import ru.endlesscode.bukkitgradle.TestBase

import static org.junit.Assert.*

class RunConfigurationTest extends TestBase {
    @Test
    void testDefaultConfiguration() {
        this.project.bukkit.run.with {
            assertFalse eula
            assertFalse onlineMode
            assertTrue debug
            assertEquals("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dfile.encoding=UTF-8 -Xmx1G", javaArgs)
            assertEquals("", bukkitArgs)
        }
    }

    @Test
    void testCustomConfiguration() {
        configureRun()

        this.project.bukkit.run.with {
            assertTrue eula
            assertTrue onlineMode
            assertFalse debug
            assertEquals("-Dfile.encoding=CP866 -Xmx2G", javaArgs)
            assertEquals("-s 2", bukkitArgs)
        }
    }
}