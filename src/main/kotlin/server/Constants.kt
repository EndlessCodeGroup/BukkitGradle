package ru.endlesscode.bukkitgradle.server

public object ServerConstants {
    public const val FALLBACK_VERSION: String = "1.16.4"
    public const val FILE_CORE: String = "core.jar"

    public const val FILE_BUILDTOOLS: String = "BuildTools.jar"
    public const val FILE_MAVEN_METADATA: String = "maven-metadata.xml"

    public const val URL_SPIGOT_METADATA: String =
        "https://hub.spigotmc.org/nexus/content/repositories/snapshots/org/spigotmc/spigot-api/$FILE_MAVEN_METADATA"
    public const val URL_BUILDTOOLS: String =
        "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar"
}

public object PaperConstants {
    public const val FALLBACK_VERSION: String = "1.16.4"

    public const val FILE_PAPERCLIP: String = "paperclip.jar"
    public const val FILE_PAPER_VERSIONS: String = "paper-versions.json"

    public const val URL_PAPER_VERSIONS: String =
        "https://gist.githubusercontent.com/osipxd/6119732e30059241c2192c4a8d2218d9/raw/$FILE_PAPER_VERSIONS"
    public const val URL_PAPER_DEFAULT: String =
        "https://papermc.io/api/v2/projects/paper/versions/1.16.4/builds/389/downloads/paper-1.16.4-389.jar"
}
