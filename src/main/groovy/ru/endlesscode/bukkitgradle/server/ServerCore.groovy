package ru.endlesscode.bukkitgradle.server

import de.undercouch.gradle.tasks.download.Download
import de.undercouch.gradle.tasks.download.DownloadExtension
import groovy.json.JsonSlurper
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

    private static final String SERVER_HOME_PROPERTY = "server.dir"
    private static final String SERVER_HOME_ENV = "BUKKIT_DEV_SERVER_HOME"
    private static final String BUILDTOOLS_NAME = "BuildTools.jar"
    private static final String BUILDTOOLS_HOME_PROPERTY = "buildtools.dir"
    private static final String BUILDTOOLS_HOME_ENV = "BUILDTOOLS_HOME"
    private static final String MAVEN_METADATA = "maven-metadata.xml"
    private static final String PAPER_VERSIONS = "paper-versions.json"
    private static final String PAPERCLIP_FILE = "paperclip.jar"
    private static final String FALLBACK_VERSION = "1.13.2"
    private static final String PAPER_FALLBACK_VERSION = "1.12.2"

    private final Project project

    private Path bukkitGradleDir
    private boolean forceRebuild = false
    private Properties localProps = new Properties()

    private Closure<CoreType> getCoreType = { project.bukkit.run.coreType }
    private String paperBuild = "lastSuccessfulBuild"

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
        registerDownloadBuildToolsTask()
        registerBuildServerCoreTask()
        registerDownloadPaperclipTask()
        registerCoreCopyTask()
    }

    /**
     * Registers Bukkit metadata downloading task
     */
    private void registerBukkitMetaTask() {
        project.task('downloadBukkitMeta') {
            group = BukkitGradlePlugin.GROUP
            description = 'Download Bukkit metadata'

            def skip = project.gradle.startParameter.isOffline() || BukkitGradlePlugin.isTesting()
            onlyIf { !skip }
            if (skip) return

            extensions.create("download", DownloadExtension, project)
            try {
                download {
                    src "https://hub.spigotmc.org/nexus/content/repositories/snapshots/org/bukkit/bukkit/$MAVEN_METADATA"
                    dest bukkitGradleDir.toFile()
                    quiet true
                }
            } catch (Exception e) {
                logger.error("Error on bukkit meta downloading: ${e.toString()}")
            }
        }
    }

    private void registerDownloadBuildToolsTask() {
        project.task('downloadBuildTools', type: Download) {
            group = BukkitGradlePlugin.GROUP
            description = 'Download BuildTools'

            // Skip it for not spigot
            if (getCoreType() != CoreType.SPIGOT) {
                enabled = false
                return
            }

            def destDir = buildToolsPath
            if (destDir == null) {
                enabled = false
                return
            }

            src "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar"
            dest destDir.toString()
            onlyIfModified true
        }
    }

    private void registerDownloadPaperclipTask() {
        project.task('downloadPaperclip', type: Download) {
            group = BukkitGradlePlugin.GROUP
            description = 'Download paperclip'

            if (project.tasks.downloadBuildTools.enabled) {
                enabled = false
                return
            }

            def skip = project.gradle.startParameter.isOffline() || BukkitGradlePlugin.isTesting()
            onlyIf { !skip }
            if (skip) return

            extensions.create("download", DownloadExtension, project)
            try {
                download {
                    src "https://gist.githubusercontent.com/OsipXD/d9ef7020a86e69c13120fa942df8f045/raw/0858b04e9b41424fb5281a911f6c6f53d8b5d177/$PAPER_VERSIONS"
                    dest bukkitGradleDir.toFile()
                    quiet true
                    onlyIfModified true
                }
            } catch (Exception e) {
                logger.error("Error on paperclip versions list downloading: ${e.toString()}")
            }

            Path destDir = serverDir
            if (destDir == null) {
                enabled = false
                return
            }

            src "https://ci.destroystokyo.com/job/Paper/$paperBuild/artifact/paperclip.jar"
            dest bukkitGradleDir.toString()
            onlyIfModified true
        }
    }

    /**
     * Registers core building task
     */
    private void registerBuildServerCoreTask() {
        project.with {
            task('buildServerCore', type: JavaExec, dependsOn: ['downloadBuildTools', 'downloadBukkitMeta']) {
                group = BukkitGradlePlugin.GROUP
                description = 'Build server core, but only if it not contains in local maven repo'

                onlyIf {
                    if (forceRebuild) {
                        forceRebuild = false
                        return true
                    }

                    return !MavenApi.hasSpigot(getCoreVersion())
                }

                if (!tasks.downloadBuildTools.enabled || serverDir == null) {
                    enabled = false
                    return
                }

                def path = buildToolsPath.resolve(BUILDTOOLS_NAME)
                def absolutePath = path.toAbsolutePath().toString()
                if (Files.notExists(path) || Files.isDirectory(path)) {
                    logger.warn("BuildTools not found on path: '$absolutePath'\n" +
                            'BuildTools directory should contains BuildTools.jar file.')
                    enabled = false
                    return
                }

                main = '-jar'
                args(absolutePath, '--rev', getSimpleVersion())
                workingDir = path.getParent().toAbsolutePath().toString()
                standardInput = System.in
            }

            task('rebuildServerCore') {
                group = BukkitGradlePlugin.GROUP
                description = 'Force rebuild server core'
            }.doLast {
                forceRebuild = true
            }.finalizedBy tasks.buildServerCore
        }
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

                def srcDir
                def fileName
                if (getCoreType() == CoreType.SPIGOT) {
                    srcDir = MavenApi.getSpigotDir(coreVersion)
                    fileName = getSpigotCoreName()
                } else {
                    srcDir = bukkitGradleDir
                    fileName = PAPERCLIP_FILE
                }

                from srcDir
                include fileName
                rename(fileName, CORE_NAME)
                into serverDir.toString()
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
     * Returns version without revision suffix
     *
     * @return Simple version
     */
    String getSimpleVersion() {
        return simplifyVersion(coreVersion)
    }

    /**
     * Returns server directory
     *
     * @return Server directory or null if dev server location not defined
     */
    @Nullable
    Path getServerDir() {
        return getDirFromPropsOrEnv(SERVER_HOME_PROPERTY, SERVER_HOME_ENV, "Dev server location").resolve(simpleVersion)
    }

    @Nullable
    private Path getBuildToolsPath() {
        return getDirFromPropsOrEnv(BUILDTOOLS_HOME_PROPERTY, BUILDTOOLS_HOME_ENV, "BuildTools location")
    }

    @Nullable
    private Path getDirFromPropsOrEnv(String propertyName, String envVariable, String comment) {
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
    private String getCoreVersion() {
        switch (getCoreType()) {
            case CoreType.SPIGOT:
                return getSpigotCoreVersion()
            case CoreType.PAPER:
                return getPaperCoreVersion()
        }
    }

    private String getSpigotCoreVersion() {
        String version = project.bukkit.version

        if (version != Bukkit.LATEST) {
            return version
        }

        Path metaFile = bukkitGradleDir.resolve(MAVEN_METADATA)
        if (Files.notExists(metaFile)) {
            if (BukkitGradlePlugin.isTesting()) return '1.11.0'

            project.logger.warn(
                    'Server core meta not downloaded, make sure that Gradle ' +
                            'isn\'t running in offline mode.\n' +
                            "Using '$FALLBACK_VERSION' by default."
            )

            return FALLBACK_VERSION
        }

        def metadata = new XmlSlurper().parse(metaFile.toFile())
        return metadata.versioning.latest.toString()
    }

    private String getPaperCoreVersion() {
        Path versionsFile = bukkitGradleDir.resolve(PAPER_VERSIONS)
        if (Files.notExists(versionsFile)) {
            project.logger.warn(
                    'Paper versions file not downloaded, make sure that Gradle ' +
                            'isn\'t running in offline mode.\n' +
                            "Using '$PAPER_FALLBACK_VERSION' by default."
            )

            return PAPER_FALLBACK_VERSION
        }

        def jsonSlurper = new JsonSlurper()
        def object = jsonSlurper.parse(versionsFile.toFile())

        String version = simplifyVersion(project.bukkit.version)
        if (version == Bukkit.LATEST) {
            version = object.latest
        }

        def versionsBuilds = object.versions as Map
        def buildNumber = versionsBuilds."$version"
        if (buildNumber == null) {
            project.logger.warn(
                    "Paper v$version not found.\n" +
                            "Supported paper versions: ${versionsBuilds.keySet()}\n" +
                            "Using '$FALLBACK_VERSION' by default."
            )

            return FALLBACK_VERSION
        }

        paperBuild = buildNumber
        return version
    }

    private static def simplifyVersion(version) {
        return version.replace(Bukkit.REVISION_SUFFIX, '')
    }
}
