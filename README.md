BukkitGradle [![Version](https://img.shields.io/github/release/EndlessCodeGroup/BukkitGradle/all.svg?style=flat-square)](https://plugins.gradle.org/plugin/ru.endlesscode.bukkitgradle) [![Build Status](https://img.shields.io/travis/EndlessCodeGroup/BukkitGradle.svg?style=flat-square)](https://travis-ci.org/EndlessCodeGroup/BukkitGradle) [![license](https://img.shields.io/github/license/EndlessCodeGroup/BukkitGradle.svg?style=flat-square)](https://github.com/EndlessCodeGroup/BukkitGradle/blob/master/LICENSE)
============
Gradle utilities for easier writing Bukkit plugins.

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Installation](#installation)
  - [First steps](#first-steps)
- [Configuration](#configuration)
- [Repositories and Dependencies](#repositories-and-dependencies)
- [Running Dev server](#running-dev-server)
  - [Dev server configuration](#dev-server-configuration)
- [Migration Guide](#migration-guide)
  - [Update to 0.9.0](#update-to-090)
- [License](#license)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

#### Features:
- Automatically applies plugin: java
- Sets up compiler encoding to UTF-8
- Sets archivesBaseName to plugin name
- Supports APIs: Bukkit, CraftBukkit, Spigot, Paper
- Provides short extension functions to add common repositories and dependencies
- Generates plugin.yml from Gradle project information
- Allows running dev server from IDE
- Supports two cores for dev server: Spigot and Paper
- Automatically downloads and updates BuildTools or Paperclip
- Automatically copies your plugin to plugins dir on server running

#### TODO:
- Add smart dependency system

## Installation
[BukkitGradle on plugins.gradle.org](https://plugins.gradle.org/plugin/ru.endlesscode.bukkitgradle)
> **Note:** Gradle 6.6+ required

#### With new plugins mechanism
```kotlin
plugins {
  id("ru.endlesscode.bukkitgradle") version "0.9.0"
}
```

#### With buildscript and apply
```groovy
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath("gradle.plugin.ru.endlesscode:bukkit-gradle:0.9.0")
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
    id("ru.endlesscode.bukkitgradle") version "0.9.0"
}
 
// Project information
group = "com.example.myplugin"
description = "My first Bukkit plugin with Gradle"
version = "0.1"

// Let's add needed API to project
dependencies {
    compileOnly(bukkit())
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
    meta {
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
 papermc        | https://papermc.io/repo/repository/maven-public/
 dmulloy2       | https://repo.dmulloy2.net/nexus/repository/public/
 md5            | https://repo.md-5.net/content/groups/public/
 jitpack        | https://jitpack.io/
 placeholderapi | https://repo.extendedclip.com/content/repositories/placeholderapi/
 aikar          | https://repo.aikar.co/content/groups/aikar/
 codemc         | https://repo.codemc.org/repository/maven-public/

#### Dependencies:
Some dependencies also applies repo needed for them.

 Name        | Signature                                     | Applies repo
-------------|-----------------------------------------------|---------------
 spigot      | org.spigotmc:spigot:$apiVersion               | mavenLocal
 spigotApi   | org.spigotmc:spigot-api:$apiVersion           | spigot
 bukkit      | org.bukkit:bukkit:$apiVersion                 | spigot
 paperApi    | com.destroystokyo.paper:paper-api:$apiVersion | destroystokyo
 
 **Note:** `$apiVersion` - is `${version}-R0.1-SNAPSHOT` (where `$version` is `bukkit.version`)

If you need more extension-functions, [create issue][issue].

## Running Dev server
Before running server you should configure dev server location.

You can define it in `local.properties` file (that was automatically created in project directory on refresh):
```properties
# Absolute path to dev server
server.dir=/path/to/buildtools/
```

If you use Spigot (see `bukkit.server.core`) you also should specify BuildTools location. For Paper no additional actions 
needed.
```properties
# Absolute path to directory that contains BuildTools.jar
buildtools.dir=/path/to/buildtools/
```
If there no BuildTools.jar it will be automatically downloaded.

> **Tip:** you can define it globally, for all projects that uses BukkitGradle.
> Specify environment variables `BUKKIT_DEV_SERVER_HOME` 
and `BUKKIT_BUILDTOOLS_HOME`.

#### On IntelliJ IDEA
Run `:buildIdeaRun` task.
Run Configuration will be added to your IDE.
It will be automatically refreshed when you change server configurations.

![Run Configuration](http://image.prntscr.com/image/1a12a03b8ac54fccb7d5b70a335fa996.png)

#### On other IDEs
Run `:runServer` task.

### Dev server configuration
To accept EULA and change settings use `bukkit.server` section:
```groovy
bukkit {
    // INFO: Here used default values
    server {
        // Core type. It can be 'spigot' or 'paper'
        core = "spigot"
        // Server version
        version = "1.16.4" // If not specified, apiVersion will be used
        // Accept EULA
        eula = false
        // Set online-mode flag
        onlineMode = false
        // Debug mode (listen 5005 port, if you use running from IDEA this option will be ignored)
        debug = true
        // Set server encoding (flag -Dfile.encoding)
        encoding = "UTF-8"
        // JVM arguments
        javaArgs("-Xmx1G")
        // Bukkit arguments
        bukkitArgs("nogui")
    }
}
```
EULA and online-mode settings in `build.gradle` always rewrites settings in `eula.txt` and `server.properties`

## Migration Guide

### Update to 0.9.0

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

[issue]: https://github.com/EndlessCodeGroup/BukkitGradle/issues/new
