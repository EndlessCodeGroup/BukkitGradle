package ru.endlesscode.bukkitgradle.server.legacy

import org.gradle.api.Project
import ru.endlesscode.bukkitgradle.server.ServerProperties
import ru.endlesscode.bukkitgradle.server.legacy.util.MavenApi

import javax.annotation.Nullable

class ServerCore {

    private ServerProperties serverProperties
    private String coreVersion

    ServerCore(
            Project project,
            ServerProperties serverProperties,
            String version
    ) {
        this.serverProperties = serverProperties
        this.coreVersion = version

        MavenApi.init(project)
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
}
