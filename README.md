BukkitGradle [![Version](https://img.shields.io/github/release/EndlessCodeGroup/BukkitGradle/all.svg?style=flat-square)](https://plugins.gradle.org/plugin/ru.endlesscode.bukkitgradle) [![Build Status](https://img.shields.io/travis/EndlessCodeGroup/BukkitGradle.svg?style=flat-square)](https://travis-ci.org/EndlessCodeGroup/BukkitGradle) [![license](https://img.shields.io/github/license/EndlessCodeGroup/BukkitGradle.svg?style=flat-square)](https://github.com/EndlessCodeGroup/BukkitGradle/blob/master/LICENSE)
============
Gradle utilities for easier writing Bukkit plugins.

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Apply plugin](#apply-plugin)
- [Usage](#usage)
  - [First steps](#first-steps)
  - [Configuring plugin](#configuring-plugin)
  - [Repositories and Dependencies](#repositories-and-dependencies)
  - [Running Dev server](#running-dev-server)

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

## Apply plugin
[BukkitGradle on plugins.gradle.org](https://plugins.gradle.org/plugin/ru.endlesscode.bukkitgradle)
> **NOTE:** Gradle 5.0+ required

#### With new plugins mechanism
```groovy
plugins {
  id "ru.endlesscode.bukkitgradle" version "0.8.2"
}
```

#### With buildscript and apply
```groovy
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath "gradle.plugin.ru.endlesscode:bukkit-gradle:0.8.2"
  }
}

apply plugin: "ru.endlesscode.bukkitgradle"
```

## Usage
You can clone [this example project](https://github.com/EndlessCodeGroup/BukkitGradle-Example) [**OUTDATED**], and use it for quick start.

### First steps
Simple `build.gradle` file that use BukkitGradle:
```groovy
plugins {
    id("ru.endlesscode.bukkitgradle") version "0.8.2"
}
 
// Project information
group = "com.example"
description = "My first Bukkit plugin with Gradle"
version = "0.1"

// Let's add needed API to project
dependencies {
    compileOnly(bukkit())
    // see section 'Dependencies' for more info
}
```
`compileOnly` - it's like provided scope in Maven. It means that this dependncy will not included to your final jar.
It's enough! Will be hooked latest version of Bukkit and automatically generated `plugin.yml` with next content:
```yaml
name: MyPlugin
description: My first Bukkit plugin with Gradle
main: com.example.myplugin.MyPlugin
version: 0.1
```
Main class build by pattern: `<groupId>.<lower case name>.<name>`

### Configuring plugin
You can configure attributes that will be placed to `plugin.yml`:
```groovy
// Override default configurations
bukkit {
    // Version of API (if you will not set this property, will be used latest available)
    version = "1.12.2"
 
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

Will be generated next `plugin.yml` file:
```yaml
name: MyPlugin
description: My amazing plugin, that doing nothing
main: com.example.plugin.MyPlugin
version: 1.0
website: http://www.example.com
authors: [OsipXD, Contributors]
```

Also you can add custom (unsupported by BukkitGradle) attributes like a `depend` etc.
Just create `plugin.yml` file and put custom attributes into.

#### Quotes around values
In some cases you may need put meta value in quotes. For this you can use `q` and `qq` functions.

For example, we have meta:
```groovy
meta {
    name.set(qq("Double Quoted Name"))
    description.set(q("Single quoted description"))
    url.set("http://without.quot.es/")
}
```

And will be generated:
```yaml
name: "Double Quoted Name"
description: 'Single quoted description'
website: http://without.quot.es/
```

**Note:** In Groovy you can use functions in two ways: normal - `q("value")` and without braces - `q "value"`

### Repositories and Dependencies
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

##### Repositories:
 Name           | Url
----------------|-------------------------------------------------------------------
 spigot         | https://hub.spigotmc.org/nexus/content/repositories/snapshots/   
 sk98q          | http://maven.sk89q.com/repo/                                     
 destroystokyo  | https://repo.destroystokyo.com/repository/maven-public/ 
 dmulloy2       | http://repo.dmulloy2.net/nexus/repository/public/
 md5            | http://repo.md-5.net/content/groups/public/
 vault          | http://nexus.hc.to/content/repositories/pub_releases/
 placeholderapi | http://repo.extendedclip.com/content/repositories/placeholderapi/
 aikar          | https://repo.aikar.co/content/groups/aikar/

##### Dependencies:
Some dependencies also applies repo needed for them.

 Name        | Signature                                     | Applies repo
-------------|-----------------------------------------------|---------------
 spigot      | org.spigotmc:spigot:$apiVersion               | mavenLocal
 spigotApi   | org.spigotmc:spigot-api:$apiVersion           | spigot
 bukkit      | org.bukkit:bukkit:$apiVersion                 | spigot
 paperApi    | com.destroystokyo.paper:paper-api:$apiVersion | destroystokyo
 
 **Note:** `$apiVersion` - is `${version}-R0.1-SNAPSHOT` (where `$version` is `bukkit.version`)

If you need more extension-functions, [create issue](https://github.com/EndlessCodeGroup/BukkitGradle/issues/new).

### Running Dev server
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

**TIP:** you can define it globally (for all projects that uses BukkitGradle) with environment variables `BUKKIT_DEV_SERVER_HOME` 
and `BUKKIT_BUILDTOOLS_HOME`.

##### On IntelliJ IDEA
Run `:buildIdeaRun` task. To your IDE will be added Run Configuration that will dynamically refreshes when you change 
server configurations.

![Run Configuration](http://image.prntscr.com/image/1a12a03b8ac54fccb7d5b70a335fa996.png)

##### On other IDEs
Run `:startServer` task.

#### Dev server configuration
To accept EULA and change settings use `bukkit.server` section:
```groovy
bukkit {
    // INFO: Here used default values
    server {
        // Core type. It can be 'spigot' or 'paper'
        core = "spigot"
        // Accept EULA
        eula = false
        // Set online-mode flag
        onlineMode = false
        // Debug mode (listen 5005 port, if you use running from IDEA this option will be ignored)
        debug = true
        // Set server encoding (flag -Dfile.encoding)
        encoding = "UTF-8"
        // JVM arguments
        javaArgs = "-Xmx1G"
        // Bukkit arguments
        bukkitArgs = "nogui"
    }
}
```
EULA and online-mode settings in `build.gradle` always rewrites settings in `eula.txt` and `server.properties`
