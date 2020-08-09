package ru.endlesscode.bukkitgradle.server.util

class Idea {

    static final IDEA_ENABLED = 'idea.enabled'

    private Idea() {}

    static boolean isEnabled() {
        return System.getProperty(IDEA_ENABLED) == 'true'
    }

    static String fileNameSlug(String name) {
        return name
                .replaceAll(/[^\x20-\x7E]/, '')
                .replaceAll(/[^a-zA-Z]/, '_')
    }
}
