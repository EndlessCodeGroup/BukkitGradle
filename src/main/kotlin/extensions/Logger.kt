package ru.endlesscode.bukkitgradle.extensions

import org.slf4j.Logger

internal fun Logger.warnSyntaxChanged(oldSyntax: String, newSyntax: String) {
    warn(
        """
        Syntax $oldSyntax is deprecated and will be removed in 1.0
        Replace it with: $newSyntax
        """.trimIndent()
    )
}
