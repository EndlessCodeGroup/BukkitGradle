package ru.endlesscode.bukkitgradle.server.extension

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.endlesscode.bukkitgradle.server.CoreType

class RunConfiguration implements Serializable {

    private static final String DEBUG_ARGS = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

    private transient Logger logger = LoggerFactory.getLogger("RunConfiguration")

    boolean eula = false
    boolean onlineMode = false
    boolean debug = true
    String encoding = 'UTF-8'
    String javaArgs = '-Xmx1G'
    String bukkitArgs = ''

    private CoreType coreType = CoreType.SPIGOT

    void setCore(String core) {
        try {
            coreType = CoreType.valueOf(core.toUpperCase(Locale.ENGLISH))
        } catch (IllegalArgumentException ignored) {
            logger.warn("""
                Core type '$core' not found. May be it doesn't supported by BukkitGradle yet. 
                Fallback core type is '${coreType.name()}'.
                Supported types: ${CoreType.values().join(', ')}
                Write an issue on GitHub to request support of other cores.
            """.stripIndent())
        }
    }

    CoreType getCoreType() {
        return coreType
    }

    /**
     * Returns arguments for JVM
     */
    String buildJvmArgs(boolean debug = this.debug) {
        return "${debug ? "$DEBUG_ARGS " : ''}-Dfile.encoding=$encoding $javaArgs"
    }

    @Override
    String toString() {
        return "RunConfiguration{" +
                "eula=$eula" +
                ", onlineMode=$onlineMode" +
                ", debug=$debug" +
                ", encoding='$encoding'" +
                ", javaArgs='$javaArgs'" +
                ", bukkitArgs='$bukkitArgs'" +
                ", coreType=$coreType" +
                "}"
    }
}
