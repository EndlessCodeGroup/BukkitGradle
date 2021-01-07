package ru.endlesscode.bukkitgradle.server.extension

public interface ServerConfiguration {

    public val version: String?
    public val eula: Boolean
    public val onlineMode: Boolean
    public val debug: Boolean
    public val encoding: String
    public val javaArgs: String
    public val bukkitArgs: String
    public val coreType: CoreType

    /** Returns arguments for JVM. */
    public fun buildJvmArgs(): String
}
