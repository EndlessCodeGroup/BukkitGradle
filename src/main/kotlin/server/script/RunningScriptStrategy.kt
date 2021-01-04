package ru.endlesscode.bukkitgradle.server.script

import org.gradle.internal.os.OperatingSystem

public abstract class RunningScriptStrategy {

    /** Returns script file extension. */
    protected abstract val ext: String

    /** Returns script file name. */
    public val fileName: String get() = "start.$ext"

    /** Returns script file content as multiline string. */
    public abstract fun getScriptText(jvmArgs: String, coreFileName: String, bukkitArgs: String): String

    /**
     * Builds and returns server run command
     * It is same for any OS
     *
     * @return Server run command
     */
    protected fun buildJavaCommand(jvmArgs: String, coreFileName: String, bukkitArgs: String): String {
        return "java $jvmArgs -jar $coreFileName $bukkitArgs"
    }

    /** Returns command for ProcessBuilder. */
    public abstract fun getCommand(title: String): List<String>

    internal companion object {

        /** Returns script strategy for current system. */
        fun get(): RunningScriptStrategy {
            if (OperatingSystem.current().isWindows) {
                return WindowsScriptStrategy
            }

            return BashScriptStrategy
        }
    }
}
