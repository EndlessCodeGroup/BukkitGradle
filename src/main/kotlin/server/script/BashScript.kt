package ru.endlesscode.bukkitgradle.server.script

import ru.endlesscode.bukkitgradle.server.extension.RunConfiguration

public class BashScript(configuration: RunConfiguration, version: String) : RunningScript(configuration, version) {

    override val ext: String = "sh"

    override fun getScriptText(): String {
        //language=bash
        return """
          #!/usr/bin/env bash

          cd "$( dirname "$0" )"
          ${buildRunCommand()}
          """.trimIndent()
    }

    override fun getCommand(): List<String> {
        return listOf("chmod +x $fileName", "&&", "./$fileName")
    }
}
