package ru.endlesscode.bukkitgradle.extension

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.endlesscode.bukkitgradle.server.CoreType

class RunConfiguration {

    private static final String DEBUG_ARGS = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

    private Logger logger = LoggerFactory.getLogger("RunConfiguration")

    boolean eula = false
    boolean onlineMode = false
    boolean debug = true
    String encoding = 'UTF-8'
    String javaArgs = '-Xmx1G'
    String bukkitArgs = ''

    private CoreType coreType = CoreType.SPIGOT

    void setCore(String core) {
        try {
            coreType = CoreType.valueOf(core.toUpperCase())
        } catch (IllegalArgumentException ignored) {
            logger.warn("Core type '$core' not found. May be it doesn't supported by BukkitGradle yet. " +
                    "You may write issue on GitHub to request supporting.\n" +
                    "Fallback core type is '${coreType.name().toLowerCase()}'")
        }
    }

    CoreType getCoreType() {
        return coreType
    }

    /**
     * Returns arguments for JVM
     */
    String buildJvmArgs(boolean debug = this.debug) {
        return "${debug ? "$DEBUG_ARGS " : ''}-Dfile.encoding=$encoding ${this.javaArgs}"
    }

    /**
     * Returns arguments for Bukkit.
     */
    String getBukkitArgs() {
        return bukkitArgs ?: ''
    }
}
