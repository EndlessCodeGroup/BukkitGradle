package ru.endlesscode.bukkitgradle.server

class ServerConstants {
    public static final String FILE_CORE = "core.jar"

    public static final String FILE_BUILDTOOLS = "BuildTools.jar"
    public static final String FILE_MAVEN_METADATA = "maven-metadata.xml"

    public static final String URL_SPIGOT_METADATA = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/org/spigotmc/spigot-api/$FILE_MAVEN_METADATA"
}

class PaperConstants {
    public static final String FILE_PAPERCLIP = "paperclip.jar"
    public static final String FILE_PAPER_VERSIONS = "paper-versions.json"

    public static final String URL_PAPER_VERSIONS = "https://gist.githubusercontent.com/osipxd/6119732e30059241c2192c4a8d2218d9/raw/7d2b9f6eaa982edebf1147ece8439dacd5f33d16/$FILE_PAPER_VERSIONS"
    public static final String URL_PAPER_DEFAULT = "https://papermc.io/ci/job/Paper-1.15/lastSuccessfulBuild/artifact/paperclip.jar"
}
