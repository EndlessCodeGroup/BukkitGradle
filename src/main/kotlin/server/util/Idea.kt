package ru.endlesscode.bukkitgradle.server.util

import org.gradle.api.provider.ProviderFactory

internal object Idea {

    private const val IDEA_ACTIVE: String = "idea.active"

    fun isActive(providers: ProviderFactory): Boolean {
        return providers.systemProperty(IDEA_ACTIVE).forUseAtConfigurationTime().orNull == "true"
    }

    @JvmStatic
    fun fileNameSlug(name: String): String {
        return name
            .replace(Regex("[^\\x20-\\x7E]"), "")
            .replace(Regex("[^a-zA-Z]"), "_")
    }
}
