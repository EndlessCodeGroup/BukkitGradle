package ru.endlesscode.bukkitgradle.server.extension

import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class ServerConfigurationImpl : ServerConfiguration {

    private val logger: Logger = LoggerFactory.getLogger("ServerConfigurationImpl")

    override var version: String? = null
    override var eula: Boolean = false
    override var onlineMode: Boolean = false
    override var debug: Boolean = false
    override var encoding: String = "UTF-8"

    override var javaArgs: List<String> = listOf("-Xmx1G")
    override var bukkitArgs: List<String> = emptyList()

    @Suppress("UNUSED_PARAMETER")
    @Deprecated("Core selecting is not supported anymore. Paper is always used.")
    public fun setCore(core: String) {
        logger.warn(
            """
            Server core selecting is not supported anymore. Paper is always used.
            Please, remove `bukkit.server.setCore(...)` from your build script.
            If you want to use other server core, file an issue:
              https://github.com/EndlessCodeGroup/BukkitGradle/issues/new
            """.trimIndent()
        )
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
            "}"
    }

    private companion object {
        const val ACCEPT_EULA_ARGS: String = "-Dcom.mojang.eula.agree=true"
    }
}
