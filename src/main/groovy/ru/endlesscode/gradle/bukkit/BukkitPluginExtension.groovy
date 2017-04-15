package ru.endlesscode.gradle.bukkit

class BukkitPluginExtension {
    public static final String NAME = 'bukkit'

    private static final String LATEST_VERSION = '+'
    private static final String VERSION_SUFFIX = '-R0.1-SNAPSHOT'

    String version

    BukkitPluginExtension() {
        this.version = LATEST_VERSION
    }

    String getVersion() {
        return (LATEST_VERSION == version) ? version : "$version$VERSION_SUFFIX"
    }
}
