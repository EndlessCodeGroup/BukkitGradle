package ru.endlesscode.bukkitgradle.server

import ru.endlesscode.bukkitgradle.extension.RunConfiguration

class LinuxScript extends SystemScript {
    LinuxScript(RunConfiguration configuration, String version) {
        super(configuration, version)
    }

    @Override
    protected String getExt() {
        return "sh"
    }

    @Override
    protected String getScriptText() {
        """#!/bin/sh

${buildRunCommand()}"""
    }

    @Override
    List<String> getCommand() {
        String scriptName = getFileName()
        return ["chmod +x $scriptName", "&&", "./$scriptName", "&"]
    }
}
