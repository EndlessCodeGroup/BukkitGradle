package ru.endlesscode.bukkitgradle.server.extension

public interface RunConfiguration {

    public val version: String?
    public val eula: Boolean
    public val onlineMode: Boolean
    public val debug: Boolean
    public val encoding: String
    public val javaArgs: String
    public val bukkitArgs: String
    public val coreType: CoreType

    /**
     * Sets core from string.
     * @see coreType
     */
    public fun setCore(core: String)

    /** Returns arguments for JVM. */
    public fun buildJvmArgs(): String
}
