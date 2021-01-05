package ru.endlesscode.bukkitgradle.server.legacy

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import ru.endlesscode.bukkitgradle.BukkitExtension
import ru.endlesscode.bukkitgradle.BukkitGradlePlugin
import ru.endlesscode.bukkitgradle.server.PaperConstants
import ru.endlesscode.bukkitgradle.server.ServerConstants
import ru.endlesscode.bukkitgradle.server.ServerProperties
import ru.endlesscode.bukkitgradle.server.extension.CoreType
import ru.endlesscode.bukkitgradle.server.legacy.util.MavenApi

import javax.annotation.Nullable

class ServerCore {

    private final Project project

    private File bukkitGradleDir
    private ServerProperties serverProperties
    private String coreVersion

    private Closure<CoreType> getCoreType = { project.bukkit.server.coreType }

    ServerCore(
            Project project,
            ServerProperties serverProperties,
            File bukkitGradleDir,
            String version
    ) {
        this.project = project
        this.serverProperties = serverProperties
        this.bukkitGradleDir = bukkitGradleDir
        this.coreVersion = version

        MavenApi.init(project)
    }

    /**
     * Registers needed tasks
     */
    void registerTasks() {
        registerCoreCopyTask()
    }

    /**
     * Registers core copying task
     */
    private void registerCoreCopyTask() {
        project.with {
            task('copyServerCore', type: Copy,
                    dependsOn: ['buildServerCore', 'downloadPaperclip']) {
                group = BukkitGradlePlugin.GROUP
                description = 'Copy server core to server directory'

                File srcDir
                def fileName
                if (getCoreType.call() == CoreType.SPIGOT) {
                    srcDir = MavenApi.getSpigotDir(fullVersion)
                    fileName = getSpigotCoreName()
                } else {
                    srcDir = bukkitGradleDir
                    fileName = PaperConstants.FILE_PAPERCLIP
                }

                from srcDir
                include fileName
                rename(fileName, ServerConstants.FILE_CORE)
                into serverDir
            }
        }
    }

    /**
     * Returns core file name
     *
     * @return Name of file
     */
    private String getSpigotCoreName() {
        return "spigot-${coreVersion}.jar"
    }

    /**
     * Returns server directory
     *
     * @return Server directory or null if dev server location not defined
     */
    @Nullable
    File getServerDir() {
        return serverProperties.devServerDir?.with { new File(it, coreVersion) }
    }

    private String getFullVersion() {
        return coreVersion + BukkitExtension.REVISION_SUFFIX
    }
}
