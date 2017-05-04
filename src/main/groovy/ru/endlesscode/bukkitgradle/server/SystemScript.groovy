package ru.endlesscode.bukkitgradle.server

import org.gradle.internal.os.OperatingSystem
import ru.endlesscode.bukkitgradle.extension.RunConfiguration

import java.nio.file.Files
import java.nio.file.Path

abstract class SystemScript {
    protected RunConfiguration configuration
    private String version

    SystemScript(RunConfiguration configuration, String version) {
        this.configuration = configuration
        this.version = version
    }

    /**
     * Generates script file on given directory
     *
     * @param dir The directory
     */
    void buildOn(Path dir) {
        Path scriptFile = dir.resolve(getFileName())
        if (!Files.exists(scriptFile)) {
            Files.createFile(scriptFile)
        }

        scriptFile.text = this.getScriptText()
    }

    /**
     * Returns script file name
     *
     * @return Script name
     */
    protected String getFileName() {
        return "start.${getExt()}"
    }

    /**
     * Returns script file extension
     *
     * @return File extension
     */
    protected abstract String getExt()

    /**
     * Returns script file content as multiline string
     *
     * @return Script text
     */
    protected abstract String getScriptText()

    /**
     * Builds and returns server run command
     * It is same for any OS
     *
     * @return Server run command
     */
    protected String buildRunCommand() {
        "java ${configuration.javaArgs} -jar ${ServerCore.CORE_NAME} ${configuration.bukkitArgs}"
    }

    /**
     * Returns command for ProcessBuilder
     *
     * @return Command
     */
    abstract List<String> getCommand()

    /**
     * Gets title for console window
     *
     * @return The title
     */
    protected String getTitle() {
        return "Dev Server (v$version)"
    }

    /**
     * Returns start script for current system
     *
     * @param configuration Run configuration
     * @param version Server version
     * @return The script
     */
    static SystemScript getScript(RunConfiguration configuration, String version) {
        if (OperatingSystem.current().isWindows()) {
            return new WindowsScript(configuration, version)
        }

        if (OperatingSystem.current().isMacOsX()) {
            return new MacScript(configuration, version)
        }

        return new LinuxScript(configuration, version)
    }
}
