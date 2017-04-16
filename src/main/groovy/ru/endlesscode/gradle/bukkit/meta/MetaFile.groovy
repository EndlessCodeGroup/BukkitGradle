package ru.endlesscode.gradle.bukkit.meta

import org.gradle.api.Project

class MetaFile {
    public static final String META_FILE = "plugin.yml"
    protected static final String[] META_ATTRIBUTES = [
            "name", "description", "version", "author", "authors", "website", "main"
    ]

    final List<String> lines
    final Project project

    MetaFile(Project project) {
        this.lines = []
        this.project = project

        this.init()
    }

    void removeUnnecessary
}
