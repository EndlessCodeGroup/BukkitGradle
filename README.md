BukkitGradle
[![Version](https://img.shields.io/github/release/EndlessCodeGroup/BukkitGradle/all.svg?style=flat-square)](https://plugins.gradle.org/plugin/ru.endlesscode.bukkitgradle)
[![license](https://img.shields.io/github/license/EndlessCodeGroup/BukkitGradle.svg?style=flat-square)](https://github.com/EndlessCodeGroup/BukkitGradle/blob/master/LICENSE)
============
Gradle utilities to simplify Bukkit/Spigot plugins writing and debugging.

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Installation](#installation)
  - [First steps](#first-steps)
- [Configuration](#configuration)
- [Repositories and Dependencies](#repositories-and-dependencies)
- [Running Dev server](#running-dev-server)
  - [Dev server configuration](#dev-server-configuration)
- [Migration Guide](#migration-guide)
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

#### TODO:
- Add smart dependency system

## Installation

[BukkitGradle on plugins.gradle.org](https://plugins.gradle.org/plugin/ru.endlesscode.bukkitgradle)
> **Note:** Gradle 8.0+ is required

#### With new plugins mechanism
```kotlin
plugins {
  id("ru.endlesscode.bukkitgradle") version "0.10.1"
}
```

#### With buildscript and apply
```groovy
buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath("gradle.plugin.ru.endlesscode:bukkit-gradle:0.10.1")
  }
}

apply(plugin: "ru.endlesscode.bukkitgradle")
```

#### Snapshots

If you want to use snapshots, you can add jitpack repository to `settings.gradle` and use version `develop-SNAPSHOT`:
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

### First steps
Simple `build.gradle` file that use BukkitGradle:
```kotlin
plugins {
    id("ru.endlesscode.bukkitgradle") version "0.10.1"
}
 
// Project information
group = "com.example.myplugin"
description = "My first Bukkit plugin with Gradle"
version = "0.1"

// Let's add needed API to project
dependencies {
    compileOnly(bukkitApi())
    // see section 'Dependencies' for more info
}
```
> **Note:** `compileOnly` - it's like `provided` scope in Maven.
It means that this dependency will not be included to your final jar.

It's enough!
Will be hooked the latest version of Bukkit and automatically generated `plugin.yml` with next content:
```yaml
name: MyPlugin
description: My first Bukkit plugin with Gradle
main: com.example.myplugin.MyPlugin
version: 0.1
api-version: 1.16
```
> **Note:** Main class built by following pattern: `<groupId>.<name>`

## Configuration
You can configure attributes that will be placed to `plugin.yml`:
```kotlin
// Override default configurations
bukkit {
    // Version of API (if you will not set this property, will be used latest version at moment of BukkitGradle release)
    apiVersion = "1.15.2"
 
    // Attributes for plugin.yml
    plugin {
        name.set("MyPlugin")
        description.set("My amazing plugin, that doing nothing")
        main.set("com.example.plugin.MyPlugin")
        version.set("1.0")
        url.set("http://www.example.com") // Attribute website
        authors.set(["OsipXD", "Contributors"])
    }
}
```

Will be generated `plugin.yml` file:
```yaml
name: MyPlugin
description: My amazing plugin, that doing nothing
main: com.example.plugin.MyPlugin
version: 1.0
api-version: 1.15
website: http://www.example.com
authors: [OsipXD, Contributors]
```

If you want to add unsupported by BukkitGradle attributes, like a `depend`, `commands` etc.
Create `plugin.yml` file and put custom attributes there.

## Repositories and Dependencies
BukkitGradle provides short extension-functions to add common repositories and dependencies.
There are list of its.

Usage example:
```kotlin
repositories {
    spigot() // Adds spigot repo
}

dependencies {
    compileOnly(paperApi()) // Adds paper-api dependency
}
```

#### Repositories:
 Name           | Url
----------------|-------------------------------------------------------------------
 spigot         | https://hub.spigotmc.org/nexus/content/repositories/snapshots/
 sk98q          | https://maven.sk89q.com/repo/
 papermc        | https://repo.papermc.io/repository/maven-public/
 dmulloy2       | https://repo.dmulloy2.net/nexus/repository/public/
 md5            | https://repo.md-5.net/content/groups/public/
 jitpack        | https://jitpack.io/
 placeholderapi | https://repo.extendedclip.com/content/repositories/placeholderapi/
 aikar          | https://repo.aikar.co/content/groups/aikar/
 codemc         | https://repo.codemc.org/repository/maven-public/

#### Dependencies:
Some dependencies also add a repository needed for them.

 Name        | Signature                              | Adds repository
-------------|----------------------------------------|-----------------
 spigot      | org.spigotmc:spigot:$apiVersion        | mavenLocal
 spigotApi   | org.spigotmc:spigot-api:$apiVersion    | spigot
 bukkitApi   | org.bukkit:bukkit:$apiVersion          | spigot
 paperApi    | io.papermc.paper:paper-api:$apiVersion | papermc
 
 **Note:** `$apiVersion` - is `${version}-R0.1-SNAPSHOT` (where `$version` is `bukkit.version`)

If you need more extension-functions, [create issue][issue].

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
> It is possible to configure server directory shared between multiple projects.
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

### Upgrade from 0.8.x

1. Update gradle to 6.6 or newer:
   ```shell
   $ ./gradlew wrapper --gradle-version 6.7.1
   ```
1. Use syntax `.set` in `bukkit.meta` instead of `=`:
   ```diff
   bukkit {
       meta {
   -        desctiption = "My plugin's description"
   +        description.set("My plugin's description")
       }
   }
   ```
1. Use `bukkit.apiVersion` instead of `bukkit.version`:
   ```diff
   bukkit {
   -   version = "1.16.4"
   +   apiVersion = "1.16.4"
   }
   ```
1. Use `build.server` block instead of `build.run`:
   ```diff
   bukkit {
   -   run {
   +   server {
           core = "paper"
       }
   }
   ```
1. Update arguments assignment syntax:
   ```diff
   bukkit {
       server {
   -       jvmArgs = "-Xmx2G -Xms512M"
   +       jvmArgs = ["-Xmx2G", "-Xms512M"]
   +       //or jvmArgs("-Xms512M") if you don't want to override defaults
       }
   }
   ```
1. Replace removed APIs:
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
1. Remove `q` and `qq` functions calls in `meta { ... }`
1. Check generated plugin.yml contents after build.
   
If there are any problems, [create an issue][issue].

## License

[MIT](LICENSE) (c) 2020 EndlessCode Group

[jpenilla/run-task]: https://github.com/jpenilla/run-task/
[issue]: https://github.com/EndlessCodeGroup/BukkitGradle/issues/new
