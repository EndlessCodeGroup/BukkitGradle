BukkitGradle [![Build Status](https://img.shields.io/travis/EndlessCodeGroup/BukkitGradle.svg)](https://travis-ci.org/EndlessCodeGroup/BukkitGradle) [![license](https://img.shields.io/github/license/EndlessCodeGroup/BukkitGradle.svg)](https://github.com/EndlessCodeGroup/BukkitGradle/blob/master/LICENSE)
============
Gradle utilities for easier writing Bukkit plugins.

## Table of Contents
1. [Apply plugin](#apply-plugin)
2. [Usage](#usage)
    1. [First steps](#first-steps)
    2. [Configuring plugin](#configuring-plugin)
    3. [Running Dev server](#running-dev-server)
        1. [Server run configurations](#server-run-configurations)

#### Features:
- Automatically applies plugins: java, idea, eclipse
- Sets up compiler encoding to UTF-8
- Adds repositories: mavenCentral, mavenLocal, spigot-repo, sk89q-repo
- Provides short extension-functions to include bukkit/craftbukkit/spigot/spigot-api
- Generates plugin.yml from Gradle project information
- Allows to run dev server from IDE
- Automatically copies your plugin to plugins dir on server running

#### TODO:
- Add extension function for PaperApi
- Add possibility to use Paper/CraftBukkit as dev server core
- Add automatically downloading of BuildTools
- Add smart dependency system

## Apply plugin
[BukkitGradle on plugins.gradle.org](https://plugins.gradle.org/plugin/ru.endlesscode.bukkitgradle)
#### With new plugins mechanism
```groovy
plugins {
  id "ru.endlesscode.bukkitgradle" version "0.7.1"
}
```

#### With buildscript and apply
```groovy
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath "gradle.plugin.ru.endlesscode:bukkit-gradle:0.7.1"
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
    id "ru.endlesscode.bukkitgradle" version "0.7.1"
}
 
// Project information
group "com.example"
description "My first Bukkit plugin with Gradle"
version "0.1"

// Let's add needed API to project
dependencies {
    compileOnly bukkit() 
    // You also can use craftbukkit(), spigot() and spigotApi()
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
    // Version of API (latest by default)
    version = "1.12.2"
 
    // Attributes for plugin.yml
    meta {
        name = "MyPlugin"
        description = "My amazing plugin, that doing nothing"
        main = "com.example.plugin.MyPlugin"
        version = "1.0"
        url = "http://www.example.com" // Attribute website
        authors = ["OsipXD", "Contributors"]
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

### Running Dev server
Before running server you should configure BuildTools and dev server location.

You can define it in `local.properties` file (that was automatically created in project root on refresh):
```properties
# Absolute path to directory that contains BuildTools.jar
buildtools.dir=/path/to/buildtools/
# Absolute path to dev server
server.dir=/path/to/buildtools/
```
Or you can define it globally (for all projects that uses BukkitGradle) with environment variables `BUKKIT_DEV_SERVER_HOME` 
and `BUILDTOOLS_HOME`.

##### On IntelliJ IDEA
Run `:buildIdeaRun` task. To your IDE will be added Run Configuration that will dynamically refreshes when you change 
server configurations.

![Run Configuration](http://image.prntscr.com/image/1a12a03b8ac54fccb7d5b70a335fa996.png)

##### On other IDEs
Run ':startServer' task.

#### Server run configurations
To accept EULA and change settings use `bukkit.run` section:
```groovy
bukkit {
    // INFO: Here used default values
    run {
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
       bukkitArgs = ""
    }
}
```
EULA and online-mode settings in `build.gradle` always rewrites settings in `eula.txt` and `server.properties`
