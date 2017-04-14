package ru.endlesscode.gradle.bukkit

class BukkitPluginExtension {
    static final String NAME = 'bukkit'

    String version

    BukkitPluginExtension() {
        this.version = '1.+'
    }
}
