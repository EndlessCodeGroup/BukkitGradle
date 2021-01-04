package ru.endlesscode.bukkitgradle.server.script

import org.gradle.internal.os.OperatingSystem
import ru.endlesscode.bukkitgradle.server.ServerConstants
import ru.endlesscode.bukkitgradle.server.extension.RunConfiguration
import java.io.File

public abstract class RunningScript(
    private val configuration: RunConfiguration,
    version: String
) {

    /** Returns title for console window. */
    protected val title: String = "Dev Server (v$version)"

    /** Returns script file extension. */
    protected abstract val ext: String

    /** Returns script file name. */
    protected val fileName: String get() = "start.$ext"

    /** Generates script file in the given [directory]. */
    public fun buildOn(directory: File) {
        val scriptFile = File(directory, fileName)
        if (!scriptFile.exists()) {
            scriptFile.createNewFile()
        }

        scriptFile.writeText(this.getScriptText())
    }

    /** Returns script file content as multiline string. */
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
