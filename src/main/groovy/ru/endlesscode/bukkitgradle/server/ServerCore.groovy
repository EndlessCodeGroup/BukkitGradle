package ru.endlesscode.bukkitgradle.server

import de.undercouch.gradle.tasks.download.DownloadExtension
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec
import ru.endlesscode.bukkitgradle.BukkitGradlePlugin
import ru.endlesscode.bukkitgradle.extension.Bukkit
import ru.endlesscode.bukkitgradle.util.MavenApi

import javax.annotation.Nullable
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ServerCore {
    public static final String CORE_NAME = "core.jar"
    public static final String SERVER_HOME_PROPERTY = "server.dir"
    public static final String SERVER_HOME_ENV = "BUKKIT_DEV_SERVER_HOME"
    public static final String BUILDTOOLS_NAME = "BuildTools.jar"
    public static final String BUILDTOOLS_HOME_PROPERTY = "buildtools.dir"
    public static final String BUILDTOOLS_HOME_ENV = "BUILDTOOLS_HOME"

    private static final String MAVEN_METADATA = "maven-metadata.xml"

    private final Project project

    private Path bukkitGradleDir
    private boolean forceRebuild = false
    private Properties localProps = new Properties()

    ServerCore(Project project) {
        this.project = project

        MavenApi.init(project)
        this.initDir()

        project.afterEvaluate {
            this.registerTasks()
        }
    }

    /**
     * Initializes Bukkit Gradle dir
     */
    void initDir() {
        this.bukkitGradleDir = project.buildDir.toPath().resolve("bukkit-gradle")
        Files.createDirectories(bukkitGradleDir)
    }

    /**
     * Registers needed tasks
     */
    void registerTasks() {
        registerBukkitMetaTask()
        registerBuildServerCoreTask()
        registerCoreCopyTask()
    }

    /**
     * Registers Bukkit metadata downloading task
     */
    void registerBukkitMetaTask() {
        project.task('downloadBukkitMeta') {
            group = BukkitGradlePlugin.GROUP
            description = 'Download Bukkit metadata'

            def skip = project.gradle.startParameter.isOffline() || BukkitGradlePlugin.isTesting()
            onlyIf { !skip }
            if (skip) return

            extensions.create("download", DownloadExtension, project)

            download {
                src "https://hub.spigotmc.org/nexus/content/repositories/snapshots/org/bukkit/bukkit/$MAVEN_METADATA"
                dest bukkitGradleDir.toFile()
                quiet true
            }
        }
    }

    /**
     * Registers core copying task
     */
    void registerCoreCopyTask() {
        project.with {
            task('copyServerCore', type: Copy,
                    dependsOn: ['buildServerCore']) {
                group = BukkitGradlePlugin.GROUP
                description = 'Copy built server core to server directory'

                if (!tasks.buildServerCore.enabled) {
                    enabled = false
                    return
                }

                def coreName = getCoreName()
                from MavenApi.getSpigotDir(realVersion)
                include coreName
                rename(coreName, CORE_NAME)
                into getServerDir().toString()
            }
        }
    }

    /**
     * Registers core building task
     */
    void registerBuildServerCoreTask() {
        project.task('buildServerCore', type: JavaExec, dependsOn: 'downloadBukkitMeta') {
            group = BukkitGradlePlugin.GROUP
            description = 'Build server core, but only if it not contains in local maven repo'

            onlyIf {
                if (forceRebuild) {
                    forceRebuild = false
                    return true
                }

                return !MavenApi.hasSpigot(getRealVersion())
            }

            if (buildToolsPath == null || serverDir == null) {
                project.logger.warn("You can't use server running feature.")
                enabled = false
                return
            }

            def path = buildToolsPath.resolve(BUILDTOOLS_NAME)
            def absolutePath = path.toAbsolutePath().toString()
            if (Files.notExists(path) || Files.isDirectory(path)) {
                project.logger.warn("BuildTools not found on path: '$absolutePath'\n" +
                        'BuildTools directory should contains BuildTools.jar file.')
                enabled = false
                return
            }

            main = '-jar'
            args(absolutePath, '--rev', getSimpleVersion())
            workingDir = path.getParent().toAbsolutePath().toString()
            standardInput = System.in
        }

        project.task('rebuildServerCore') {
            group = BukkitGradlePlugin.GROUP
            description = 'Force rebuild server core'
        }.doLast {
            forceRebuild = true
        }.finalizedBy project.tasks.buildServerCore
    }

    /**
     * Returns core file name
     *
     * @return Name of file
     */
    String getCoreName() {
        return "spigot-${getRealVersion()}.jar"
    }

    /**
     * Returns version without revision suffix
     *
     * @return Simple version
     */
    String getSimpleVersion() {
        return getRealVersion().replace(Bukkit.REVISION_SUFFIX, '')
    }

    /**
     * Returns server directory
     *
     * @return Server directory or null if dev server location not defined
     */
    @Nullable
    Path getServerDir() {
        return getDirFromPropsOrEnv(SERVER_HOME_PROPERTY, SERVER_HOME_ENV, "Dev server location")
    }

    private @Nullable
    Path getBuildToolsPath() {
        return getDirFromPropsOrEnv(BUILDTOOLS_HOME_PROPERTY, BUILDTOOLS_HOME_ENV, "BuildTools location")
    }

    private @Nullable
    Path getDirFromPropsOrEnv(String propertyName, String envVariable, String comment) {
        this.initLocalProps()

        def localProp = localProps.getProperty(propertyName)
        def globalEnv = System.getenv(envVariable)
        if (localProp == null && globalEnv == null) {
            project.logger.warn("$comment not found. It can be fixed by two ways:\n" +
                    "   1. Define location with '$propertyName' in the local.properties file\n" +
                    "   2. Define $envVariable environment variable")
            return null
        }
        def dir = Paths.get(localProp ?: globalEnv)
        Files.createDirectories(dir)

        return dir
    }

    private initLocalProps() {
        Path propsFile = this.project.file("local.properties").toPath()
        if (Files.exists(propsFile)) {
            localProps.load(propsFile.newReader())
            return
        }

        project.logger.info("Local properties file not found. Creating...")
        Files.createFile(propsFile)
        localProps.load(propsFile.newReader())

        if (System.getenv(SERVER_HOME_ENV) == null) {
            localProps.setProperty(SERVER_HOME_PROPERTY, project.file("build/server").absolutePath)
        }

        if (System.getenv(BUILDTOOLS_HOME_ENV) == null) {
            localProps.setProperty(BUILDTOOLS_HOME_PROPERTY, project.file("build/buildtools").absolutePath)
        }

        localProps.store(propsFile.newWriter(), " This file should *NOT* be checked into Version Control Systems,\n" +
                " as it contains information specific to your local configuration.\n" +
                " \n" +
                " Location of the dev server and BuildTools.\n" +
                " For customization when using a Version Control System, please read the\n" +
                " header note."
        )
    }

    /**
     * Resolves and returns dynamic version
     *
     * @return Real Bukkit version
     */
    private String getRealVersion() {
        String version = project.bukkit.version
        if (version != Bukkit.LATEST) {
            return version
        }

        Path metaFile = bukkitGradleDir.resolve(MAVEN_METADATA)
        if (Files.notExists(metaFile)) {
            if (BukkitGradlePlugin.isTesting()) return '1.11.0'

            def defaultVersion = '1.12.2'
            project.logger.warn(
                    'Server cores meta not downloaded, make sure that Gradle ' +
                            'isn\'t running in offline mode.\n' +
                            "Using '$defaultVersion' by default."
            )

            return defaultVersion
        }

        def metadata = new XmlSlurper().parse(metaFile.toFile())
        return metadata.versioning.latest.toString()
    }
}
