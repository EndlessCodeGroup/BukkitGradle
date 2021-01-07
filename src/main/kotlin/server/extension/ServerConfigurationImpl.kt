package ru.endlesscode.bukkitgradle.server.extension

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

public class ServerConfigurationImpl : ServerConfiguration {

    private val logger: Logger = LoggerFactory.getLogger("ServerConfigurationImpl")

    override var version: String? = null
    override var eula: Boolean = false
    override var onlineMode: Boolean = false
    override var debug: Boolean = true
    override var encoding: String = "UTF-8"

    override var javaArgs: List<String> = listOf("-Xmx1G")
    override var bukkitArgs: List<String> = listOf("nogui")

    override var coreType: CoreType = CoreType.SPIGOT

    /**
     * Sets core from string.
     * @see coreType
     */
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

    /** Append the given [args] to `javaArgs`. */
    public fun javaArgs(vararg args: String) {
        javaArgs = javaArgs + args.toList()
    }

    /** Append the given [args] to `bukkitArgs`. */
    public fun bukkitArgs(vararg args: String) {
        bukkitArgs = bukkitArgs + args.toList()
    }

    override fun buildJvmArgs(debug: Boolean): List<String> {
        return listOfNotNull(DEBUG_ARGS.takeIf { debug }, "-Dfile.encoding=$encoding") + javaArgs
    }

    override fun toString(): String {
        return "ServerConfigurationImpl{" +
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
