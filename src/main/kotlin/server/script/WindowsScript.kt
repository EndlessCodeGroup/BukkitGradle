package ru.endlesscode.bukkitgradle.server.script

import ru.endlesscode.bukkitgradle.server.extension.RunConfiguration

public class WindowsScript(configuration: RunConfiguration, version: String) : RunningScript(configuration, version) {

    override val ext: String = "bat"

    override fun getScriptText(): String {
        //language=bat
        return """
            @echo off
            ${buildRunCommand()}
            pause
            exit
            """.trimIndent()
    }

    @Override
    override fun getCommand(): List<String> {
        val title = "\"${title}\""
        return listOf("cmd", "/c", "start", title, fileName)
    }
}
