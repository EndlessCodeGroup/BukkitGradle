BukkitGradle
[![Version](https://img.shields.io/github/release/EndlessCodeGroup/BukkitGradle/all.svg?style=flat-square)](https://plugins.gradle.org/plugin/ru.endlesscode.bukkitgradle)
[![license](https://img.shields.io/github/license/EndlessCodeGroup/BukkitGradle.svg?style=flat-square)](https://github.com/EndlessCodeGroup/BukkitGradle/blob/master/LICENSE)
============
Gradle utilities to simplify Bukkit/Spigot plugins writing and debugging.

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Installation](#installation)
  - [Quick Start](#quick-start)
- [Configuration](#configuration)
- [Repositories and Dependencies](#repositories-and-dependencies)
- [Running Dev server](#running-dev-server)
  - [Dev server configuration](#dev-server-configuration)
- [Migration Guide](#migration-guide)
  - [Upgrade from 0.10.x](#upgrade-from-010x)
  - [Upgrade from 0.8.x](#upgrade-from-08x)
- [License](#license)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

#### Features:

- Sets up compiler encoding to UTF-8
- Sets archivesBaseName to plugin name
- Supports APIs: Bukkit, CraftBukkit, Spigot, Paper
- Provides short extension functions to add common repositories and dependencies
- Generates plugin.yml from Gradle project information
- Allows running dev server from IDE
- Runs server using [jpenilla/run-task]

## Installation

> [!NOTE]
> BukkitGradle requires Gradle 8.0+ to run

```kotlin
plugins {
    id("ru.endlesscode.bukkitgradle") version "0.10.1"
}
```

[BukkitGradle on plugins.gradle.org](https://plugins.gradle.org/plugin/ru.endlesscode.bukkitgradle)

<details>

<summary>Using snapshots</summary>

To use snapshots, add jitpack repository to the `settings.gradle.kts` and specify version `develop-SNAPSHOT`:

```kotlin
// settings.gradle

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io") }
    }
}

rootProject.name = "<your project name>"
```

```kotlin
// build.gradle

plugins {
    id("ru.endlesscode.bukkitgradle") version "develop-SNAPSHOT"
}
```

</details>

### Quick Start

Apply the plugin and configure project's `group`, `description` and `version`.
These values will be used to generate the `plugin.yml` file:

```kotlin
plugins {
    id("ru.endlesscode.bukkitgradle") version "0.10.1"
}

group = "com.example.myplugin"
description = "My first Bukkit plugin built by Gradle"
version = "0.1"

bukkit {
    apiVersion = "1.16.5"
}

// Add the necessary API to the project
dependencies {
    compileOnly(paperApi())
    // See the 'Dependencies' section for more info
}

repositories {
    papermc()
}
```

That's it!
During the plugin compilation `plugin.yml` will be generated with the following content:

```yaml
api-version: '1.16'
name: MyPlugin
version: '0.1'
main: com.example.myplugin.MyPlugin
description: My first Bukkit plugin built by Gradle
```

> [!NOTE]
> By default, main class is built by the following pattern: `<groupId>.<name>`

Next, you might need to [configure](#configuration) `plugin.yml` content or [run dev server](#running-dev-server).

## Configuration

The `plugin.yml` content can be configured using `bukkit.plugin { ... }` block.

```kotlin
bukkit {
    // Version of API. By default, 1.16.5 is used
    apiVersion = "1.15.2"

    // Configure plugin.yml content
    plugin {
        name = "MyPlugin"
        description = "My amazing plugin"
        main = "com.example.plugin.MyPlugin"
        version = "1.0"
        authors = listOf("osipxd", "contributors")
        depend = listOf("Vault", "Mimic")
    }
}
```

## Repositories and Dependencies

BukkitGradle provides shortcuts to add common repositories and dependencies:

```kotlin
repositories {
    papermc()
}

dependencies {
    compileOnly(paperApi)
}
```

#### Repositories

| Name             | Url                                                                |
|------------------|--------------------------------------------------------------------|
| `spigot`         | https://hub.spigotmc.org/nexus/content/repositories/snapshots/     |
| `sk98q`          | https://maven.sk89q.com/repo/                                      |
| `papermc`        | https://repo.papermc.io/repository/maven-public/                   |
| `dmulloy2`       | https://repo.dmulloy2.net/nexus/repository/public/                 |
| `md5`            | https://repo.md-5.net/content/groups/public/                       |
| `jitpack`        | https://jitpack.io/                                                |
| `placeholderapi` | https://repo.extendedclip.com/content/repositories/placeholderapi/ |
| `aikar`          | https://repo.aikar.co/content/groups/aikar/                        |
| `codemc`         | https://repo.codemc.org/repository/maven-public/                   |

#### Dependencies

When adding dependencies, make sure you've added the corresponding repository.

| Name        | Signature                                | Official repository |
|-------------|------------------------------------------|---------------------|
| `spigot`    | `org.spigotmc:spigot:$apiVersion`        | `mavenLocal()`*     |
| `spigotApi` | `org.spigotmc:spigot-api:$apiVersion`    | `spigot()`          |
| `bukkitApi` | `org.bukkit:bukkit:$apiVersion`          | `spigot()`          |
| `paperApi`  | `io.papermc.paper:paper-api:$apiVersion` | `papermc()`         |

\* Spigot is available in `mavenLocal()` only if you've built it locally using [Spigot BuildTools][buildtools]. \
\*\* `$apiVersion` - is `${version}-R0.1-SNAPSHOT` (where `$version` is `bukkit.apiVersion`)

If you need more shortcuts, [file an issue][issue].

## Running Dev server

This plugin pre-configures [jpenilla/run-task] according to the specified [configuration](#dev-server-configuration).
Use `:runServer` task to run the dev server:

```bash
./gradlew runServer
```

> [!TIP]
> It is possible to create a run configuration for IDEA by running `:buildIdeaRun` task.
> The configuration will be stored in `<projectDir>/.run` directory so it can be shared through VCS.
> The directory can be changed by configuring the `:buildIdeaRun` task:
> ```kotlin
> tasks.buildIdeaRun {
>     configurationsDir = file(".idea/runConfigurations")
> }
> ```

By default, the server will be located at `<projectDir>/run` but you can change it by providing Gradle property `bukkitgradle.server.dir`:

```properties
# gradle.properties
bukkitgradle.server.dir=build/run
```

Alternatively, you can configure `runServer` task:

```kotlin
tasks.runServer {
    runDirectory.set(file("build/run"))
}
```

> [!TIP]
> It is possible to configure a server directory shared between multiple projects.
> Set the `bukkitgradle.server.dir` property in `$HOME/.gradle/gradle.properties`.
>
> This file contains local configurations to be used for all Gradle projects.
> The value specified in project's `gradle.properties` takes precedence over the global one.

### Dev server configuration

Use `bukkit.server` section to accept EULA and configure the server:

```groovy
bukkit {
    // INFO: Default values are used here
    server {
        // Server version
        version = "1.16.4" // If not specified, bukkit.apiVersion will be used
        // Accept EULA
        eula = false
        // Set online-mode flag
        onlineMode = false
        // Debug mode (listen to 5005 port)
        debug = true
        // Set default file encoding (flag -Dfile.encoding)
        encoding = "UTF-8"
        // JVM arguments
        javaArgs("-Xmx1G")
        // Bukkit arguments
        bukkitArgs()
    }
}
```

> [!NOTE]
> `eula` and `online-mode` options specified in `bukkit.server` always take precedence over the values specified
> in `eula.txt` and `server.properties`

## Migration Guide

### Upgrade from 0.10.x

1. Update Gradle to 8.0 or newer (the latest version is recommended):
   ```shell
   ./gradlew wrapper --gradle-version 8.13
   ```

2. Replace deprecated and removed APIs:
   ```diff
   bukkit {
   -   meta {
   +   plugin {
           name = "MyPlugin"
   -       url = "https://example.com/"
   +       website = "https://example.com/"
       }
   }
   ``` 

3. Remove server core selection: `bukkit.server.coreType` and `bukkit.server.setCore(...)`.
   Paper is the only supported server core now.

4. If you have `plugin.yml`, move it's content to `bukkit.plugin { ... }` block and delete the file.

5. Explicitly add `mavenCentral()` to the repositories if you're using dependencies from it:
   ```kotlin
   repositories {
       mavenCentral()
   } 
   ```
   Add repositories for Bukkit/Spigot/Paper according to the [dependency table](#dependencies).

### Upgrade from 0.8.x

1. Use `bukkit.apiVersion` instead of `bukkit.version`:
   ```diff
   bukkit {
   -   version = "1.16.4"
   +   apiVersion = "1.16.4"
   }
   ```
2. Use `build.server` block instead of `build.run`:
   ```diff
   bukkit {
   -   run {
   +   server {
           core = "paper"
       }
   }
   ```
3. Update arguments assignment syntax:
   ```diff
   bukkit {
       server {
   -       jvmArgs = "-Xmx2G -Xms512M"
   +       jvmArgs = ["-Xmx2G", "-Xms512M"]
   +       //or jvmArgs("-Xms512M") if you don't want to override defaults
       }
   }
   ```
4. Replace removed APIs:
   ```diff
   repositories {
   -   destroystokyo()
   +   papermc()

   -   vault()
   +   jitpack()
   }
   
   dependencies {
   -   compileOnly(craftbikkit())
   +   compileOnly(spigot())
   }
   ```
5. Remove `q` and `qq` functions calls in `meta { ... }`
6. Check generated plugin.yml contents after build.

If there are any problems, [create an issue][issue].

## License

[MIT](LICENSE) (c) 2020 EndlessCode Group

[jpenilla/run-task]: https://github.com/jpenilla/run-task/
[buildtools]: https://www.spigotmc.org/wiki/buildtools/
[issue]: https://github.com/EndlessCodeGroup/BukkitGradle/issues/new
