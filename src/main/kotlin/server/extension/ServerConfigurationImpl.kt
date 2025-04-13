package ru.endlesscode.bukkitgradle.server.extension

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

public class ServerConfigurationImpl : ServerConfiguration {

    private val logger: Logger = LoggerFactory.getLogger("ServerConfigurationImpl")

    override var version: String? = null
    override var eula: Boolean = false
    override var onlineMode: Boolean = false
    override var debug: Boolean = false
    override var encoding: String = "UTF-8"

    override var javaArgs: List<String> = listOf("-Xmx1G")
    override var bukkitArgs: List<String> = emptyList()

    override var coreType: CoreType = CoreType.SPIGOT

    /**
     * Sets core from string.
     * @see coreType
     */
    public fun setCore(core: String) {
        try {
            coreType = CoreType.valueOf(core.uppercase(Locale.ENGLISH))
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

    override fun buildJvmArgs(): List<String> {
        return listOfNotNull(
            ACCEPT_EULA_ARGS.takeIf { eula },
        ) + javaArgs
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
        const val ACCEPT_EULA_ARGS: String = "-Dcom.mojang.eula.agree=true"
    }
}
