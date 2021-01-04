package ru.endlesscode.bukkitgradle.server.script

public object WindowsScriptStrategy : RunningScriptStrategy() {

    override val ext: String = "bat"

    override fun getScriptText(jvmArgs: String, bukkitArgs: String): String {
        //language=bat
        return """
            @echo off
            ${buildJavaCommand(jvmArgs, bukkitArgs)}
            pause
            exit
            """.trimIndent()
    }

    override fun getCommand(title: String): List<String> {
        val title = "\"${title}\""
        return listOf("cmd", "/c", "start", title, fileName)
    }
}
