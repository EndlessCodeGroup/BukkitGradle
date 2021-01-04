package ru.endlesscode.bukkitgradle.server.extension

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

public class RunConfiguration {

    private val logger: Logger = LoggerFactory.getLogger("RunConfiguration")

    public var eula: Boolean = false
    public var onlineMode: Boolean = false
    public var debug: Boolean = true
    public var encoding: String = "UTF-8"
    public var javaArgs: String = "-Xmx1G"
    public var bukkitArgs: String = ""

    private var coreType: CoreType = CoreType.SPIGOT

    public fun setCore(core: String) {
        try {
            coreType = CoreType.valueOf(core.toUpperCase(Locale.ENGLISH))
        } catch (_: IllegalArgumentException) {
            logger.warn(
                """
                Core type '$core' not found. May be it doesn't supported by BukkitGradle yet.
                Fallback core type is '${coreType.name}'.
                Supported types: ${CoreType.values().joinToString(", ")}
                Write an issue on GitHub to request support of other cores.
                """.trimIndent()
            )
        }
    }

    public fun getCoreType(): CoreType {
        return coreType
    }

    /** Returns arguments for JVM. */
    @JvmOverloads
    public fun buildJvmArgs(debug: Boolean = this.debug): String {
        return "${if (debug) "$DEBUG_ARGS " else ""}-Dfile.encoding=$encoding $javaArgs"
    }

    override fun toString(): String {
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

    private companion object {
        const val DEBUG_ARGS: String = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
    }
}
