package ru.endlesscode.bukkitgradle.server.extension

public interface ServerConfiguration {

    public val version: String?
    public val eula: Boolean
    public val onlineMode: Boolean
    public val debug: Boolean
    public val encoding: String
    public val javaArgs: List<String>
    public val bukkitArgs: List<String>
    public val coreType: CoreType

    /** Returns arguments for JVM. */
    public fun buildJvmArgs(): List<String>
}
