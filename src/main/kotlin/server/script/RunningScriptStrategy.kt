package ru.endlesscode.bukkitgradle.server.script

import org.gradle.internal.os.OperatingSystem

internal abstract class RunningScriptStrategy {

    /** Returns script file extension. */
    protected abstract val ext: String

    /** Returns script file name. */
    val fileName: String get() = "start.$ext"

    /** Returns script file content as multiline string. */
    abstract fun getScriptText(jvmArgs: String, coreFileName: String, bukkitArgs: String): String

    /**
     * Builds and returns server run command
     * It is same for any OS
     *
     * @return Server run command
     */
    protected fun buildJavaCommand(jvmArgs: String, coreFileName: String, bukkitArgs: String): String {
        return "java $jvmArgs -jar $coreFileName $bukkitArgs".trimEnd()
    }

    /** Returns command for ProcessBuilder. */
    abstract fun getCommand(fileName: String, title: String): List<String>

    companion object {

        /** Returns script strategy for current system. */
        fun get(operatingSystem: OperatingSystem): RunningScriptStrategy {
            if (operatingSystem.isWindows) {
                return WindowsScriptStrategy
            }

            return BashScriptStrategy
        }
    }
}
