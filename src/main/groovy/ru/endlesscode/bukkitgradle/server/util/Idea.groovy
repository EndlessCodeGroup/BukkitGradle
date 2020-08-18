package ru.endlesscode.bukkitgradle.server.util

class Idea {

    static final IDEA_ACTIVE = 'idea.active'

    private Idea() {}

    static boolean isActive() {
        return System.getProperty(IDEA_ACTIVE) == 'true'
    }

    static String fileNameSlug(String name) {
        return name
                .replaceAll(/[^\x20-\x7E]/, '')
                .replaceAll(/[^a-zA-Z]/, '_')
    }
}
