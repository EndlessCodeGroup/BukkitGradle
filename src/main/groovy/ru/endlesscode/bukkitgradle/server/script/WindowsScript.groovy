package ru.endlesscode.bukkitgradle.server.script

import ru.endlesscode.bukkitgradle.extension.RunConfiguration

class WindowsScript extends SystemScript {
    WindowsScript(RunConfiguration configuration, String version) {
        super(configuration, version)
    }

    @Override
    protected String getExt() {
        return "bat"
    }

    @Override
    protected String getScriptText() {
        """@echo off
${this.buildRunCommand()}
pause
exit"""
    }

    @Override
    List<String> getCommand() {
        String title = "\"${getTitle()}\""
        return ["cmd", "/c", "start", title, getFileName()]
    }
}
