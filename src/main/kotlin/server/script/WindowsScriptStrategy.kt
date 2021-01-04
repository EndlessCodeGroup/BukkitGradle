package ru.endlesscode.bukkitgradle.server.script

internal object WindowsScriptStrategy : RunningScriptStrategy() {

    override val ext: String = "bat"

    override fun getScriptText(jvmArgs: String, coreFileName: String, bukkitArgs: String): String {
        //language=bat
        return """
            @echo off
            ${buildJavaCommand(jvmArgs, coreFileName, bukkitArgs)}
            pause
            exit
            """.trimIndent()
    }

    override fun getCommand(title: String): List<String> {
        return listOf("cmd", "/c", "start", "\"${title}\"", fileName)
    }
}
