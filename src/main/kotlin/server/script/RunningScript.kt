package ru.endlesscode.bukkitgradle.server.script

import org.gradle.internal.os.OperatingSystem
import ru.endlesscode.bukkitgradle.server.ServerConstants
import ru.endlesscode.bukkitgradle.server.extension.RunConfiguration
import java.io.File

public abstract class RunningScript(
    protected val configuration: RunConfiguration,
    private val version: String
) {

    /**
     * Generates script file on given directory
     *
     * @param dir The directory
     */
    public fun buildOn(dir: File) {
        val scriptFile = File(dir, getFileName())
        if (!scriptFile.exists()) {
            scriptFile.createNewFile()
        }

        scriptFile.writeText(this.getScriptText())
    }

    /**
     * Returns script file name
     *
     * @return Script name
     */
    protected fun getFileName(): String = "start.${getExt()}"

    /**
     * Returns script file extension
     *
     * @return File extension
     */
    protected abstract fun getExt(): String

    /**
     * Returns script file content as multiline string
     *
     * @return Script text
     */
    protected abstract fun getScriptText(): String

    /**
     * Builds and returns server run command
     * It is same for any OS
     *
     * @return Server run command
     */
    protected fun buildRunCommand(): String {
        return "java ${configuration.buildJvmArgs()} -jar ${ServerConstants.FILE_CORE} ${configuration.bukkitArgs}"
    }

    /** Returns command for ProcessBuilder. */
    public abstract fun getCommand(): List<String>

    /** Gets title for console window. */
    protected fun getTitle(): String = "Dev Server (v$version)"

    public companion object {

        /**
         * Returns start script for current system
         *
         * @param configuration Run configuration
         * @param version Server version
         * @return The script
         */
        @JvmStatic
        public fun getScript(configuration: RunConfiguration, version: String): RunningScript {
            if (OperatingSystem.current().isWindows) {
                return WindowsScript(configuration, version)
            }

            return BashScript(configuration, version)
        }
    }
}
