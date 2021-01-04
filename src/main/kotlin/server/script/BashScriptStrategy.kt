package ru.endlesscode.bukkitgradle.server.script

internal object BashScriptStrategy : RunningScriptStrategy() {

    override val ext: String = "sh"

    override fun getScriptText(jvmArgs: String, coreFileName: String, bukkitArgs: String): String {
        //language=bash
        return """
          #!/usr/bin/env bash

          cd "$( dirname "$0" )"
          ${buildJavaCommand(jvmArgs, coreFileName, bukkitArgs)}
          """.trimIndent()
    }

    override fun getCommand(title: String): List<String> {
        return listOf("chmod +x $fileName", "&&", "./$fileName")
    }
}
