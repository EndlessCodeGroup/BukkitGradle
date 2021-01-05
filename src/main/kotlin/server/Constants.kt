package ru.endlesscode.bukkitgradle.server

public object ServerConstants {
    public const val DEFAULT_VERSION: String = "1.16.4"
    public const val FILE_CORE: String = "core.jar"
}

public object BuildToolsConstants {
    public const val URL: String =
        "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar"
}

public object PaperConstants {
    public const val URL_PAPER_VERSIONS: String =
        "https://gist.githubusercontent.com/osipxd/6119732e30059241c2192c4a8d2218d9/raw/paper-versions.jsonS"
}
