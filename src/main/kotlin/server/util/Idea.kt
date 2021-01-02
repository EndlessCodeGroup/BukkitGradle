package ru.endlesscode.bukkitgradle.server.util

internal object Idea {

    private const val IDEA_ACTIVE: String = "idea.active"

    fun isActive(): Boolean = System.getProperty(IDEA_ACTIVE) == "true"

    @JvmStatic
    fun fileNameSlug(name: String): String {
        return name
            .replace(Regex("[^\\x20-\\x7E]"), "")
            .replace(Regex("[^a-zA-Z]"), "_")
    }
}
