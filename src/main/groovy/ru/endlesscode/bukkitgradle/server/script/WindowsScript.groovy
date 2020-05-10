package ru.endlesscode.bukkitgradle.server.script

import ru.endlesscode.bukkitgradle.server.extension.RunConfiguration

class WindowsScript extends RunningScript {

    WindowsScript(RunConfiguration configuration, String version) {
        super(configuration, version)
    }

    @Override
    protected String getExt() {
        return "bat"
    }

    @Override
    protected String getScriptText() {
        //language=bat
        $/
        @echo off
        ${buildRunCommand()}
        pause
        exit
        /$.stripIndent()
    }

    @Override
    List<String> getCommand() {
        String title = "\"${getTitle()}\""
        return ["cmd", "/c", "start", title, getFileName()]
    }
}
