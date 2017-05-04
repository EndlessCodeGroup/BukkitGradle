package ru.endlesscode.bukkitgradle.server

import ru.endlesscode.bukkitgradle.extension.RunConfiguration

class MacScript extends SystemScript {
    MacScript(RunConfiguration configuration, String version) {
        super(configuration, version)
    }

    @Override
    protected String getExt() {
        return "command"
    }

    @Override
    protected String getScriptText() {
        """#!/bin/sh

cd "\$( dirname "\$0" )"
${buildRunCommand()}"""
    }

    @Override
    List<String> getCommand() {
        String scriptName = getFileName()
        return ["chmod +x $scriptName", "&&", "open", scriptName]
    }
}
