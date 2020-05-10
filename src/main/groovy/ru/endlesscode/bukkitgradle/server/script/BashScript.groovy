package ru.endlesscode.bukkitgradle.server.script

import ru.endlesscode.bukkitgradle.extension.RunConfiguration

class BashScript extends RunningScript {

    BashScript(RunConfiguration configuration, String version) {
        super(configuration, version)
    }

    @Override
    protected String getExt() {
        return "sh"
    }

    @Override
    protected String getScriptText() {
        //language=bash
        $/
        #!/usr/bin/env bash

        cd "$( dirname "$0" )"
        ${buildRunCommand()}
        /$.stripIndent()
    }

    @Override
    List<String> getCommand() {
        String scriptName = getFileName()
        return ["chmod +x $scriptName", "&&", "./$scriptName"]
    }
}
