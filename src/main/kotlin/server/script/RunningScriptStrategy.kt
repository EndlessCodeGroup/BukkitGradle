package ru.endlesscode.bukkitgradle.server.script

import org.gradle.internal.os.OperatingSystem
import ru.endlesscode.bukkitgradle.server.ServerConstants
import java.io.File

public abstract class RunningScriptStrategy {

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

        // FIXME
        //scriptFile.writeText(this.getScriptText(, ))
    }

    /** Returns script file content as multiline string. */
    protected abstract fun getScriptText(jvmArgs: String, bukkitArgs: String): String

    /**
     * Builds and returns server run command
     * It is same for any OS
     *
     * @return Server run command
     */
    protected fun buildJavaCommand(jvmArgs: String, bukkitArgs: String): String {
        return "java $jvmArgs -jar ${ServerConstants.FILE_CORE} $bukkitArgs"
    }

    /** Returns command for ProcessBuilder. */
    public abstract fun getCommand(title: String): List<String>

    public companion object {

        /** Returns start script for current system. */
        @JvmStatic
        public fun get(): RunningScriptStrategy {
            if (OperatingSystem.current().isWindows) {
                return WindowsScriptStrategy
            }

            return BashScriptStrategy
        }
    }
}
