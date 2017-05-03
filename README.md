BukkitGradle
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
- Automatically resolves needed version of bukkit api
- Generates plugin.yml from Gradle project information
- Allows to run dev server from IDE

#### TODO:
- Add smart dependency system

## Apply plugin

#### Gradle 2.1 and higher
```groovy
plugins {
  id "ru.endlesscode.bukkitgradle" version "0.6.7"
}
```

#### Any gradle versions
```groovy
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath "gradle.plugin.ru.endlesscode:bukkit-gradle:0.6.7"
  }
}

apply plugin: "ru.endlesscode.bukkitgradle"
```

## Usage
You can clone [this example project](https://github.com/EndlessCodeGroup/BukkitGradle-Example), and use it as a starting point.

### First steps
Simple `build.gradle` file that use BukkitGradle:
```groovy
plugins {
    id "ru.endlesscode.bukkitgradle" version "0.6.7"
}
 
// Project information
group "com.example"
description "My first Bukkit plugin with Gradle"
version "0.1"
```
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
    // Version of Bukkit (latest by default)
    version = "1.10.2"
 
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

Also you can add custom (unsupported by BukkitGradle) attributes like ad `depend` etc.
Just create `plugin.yml` file and put custom attributes into it.

### Running Dev server

##### On IntelliJ IDEA
Run `:buildIdeaRun` task. To your IDE will be added Run Configuration that will dynamically refreshes when you change server configurations.

##### On other IDEs
Run ':startServer' task.

#### Server run configurations
To accept EULA and change something setting use `bukkit.run` section:
```groovy
bukkit {
    // INFO: Here used default values
    run {
       // Accept EULA
       eula = false
       // Set online-mode flag
       onlineMode = false
       // Path to deploy server (relative)
       dir = "server"
       // Debug mode (listen 5005 port, if you use running from IDEA this option be ignored)
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