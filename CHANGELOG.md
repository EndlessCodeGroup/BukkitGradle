## [Unreleased]

*No changes yet*

## [1.0.0] - 2025-04-18

### Pivot!

After a 4-year hiatus in releases, the plugin landscape has evolved significantly.
jpenilla's excellent plugins [run-task](https://github.com/jpenilla/run-task/) and [resource-factory](https://github.com/jpenilla/resource-factory) have been released,
and they're perfect for running Paper servers and generating `plugin.yml`.

Rather than maintaining our own implementations, BukkitGradle now leverages jpenilla's work, allowing us to:

1. **Reduce maintenance burden**: By delegating core functionality to actively maintained projects
2. **Focus on innovation**: Instead of reinventing the wheel, we can add new features

The main goal of BukkitGradle is now to provide nice defaults and a unified API
that simplifies plugin development by seamlessly integrating these plugins.

### Features

- **Breaking change!**
  Use [jpenilla/run-task](https://github.com/jpenilla/run-task/) plugin and integrate run-paper for server execution,
  improving maintainability and compatibility with various server versions.
  - Remove `bukkit.server.coreType` property.
    Spigot is not supported anymore, PaperMC is the only supported server.
    If you need to run other server cores,
    please [file an issue](https://github.com/EndlessCodeGroup/BukkitGradle/issues/new).
- **Breaking change!**
  Use [jpenilla/resource-factory](https://github.com/jpenilla/resource-factory) under the hood to generate `plugin.yml`.
  This change enables full configuration of `plugin.yml` from a build script, but it comes with some renaming:
  - Configuration block `bukkit.meta { ... }` -> `bukkit.plugin { ... }`
  - Property `bukkit.plugin.url` -> `bukkit.plugin.website`
  - Task `:parsePluginMetaFile` -> `:parsePluginYaml`
  - Task `:mergePluginMeta` has been dropped. Use `:mainResourceFactory` instead
  - Package `ru.endlesscode.bukkitgradle.meta` -> `ru.endlesscode.bukkitgradle.plugin`
- **Breaking change!** Don't add repositories implicitly.
  It was impossible to opt out from automatic repositories adding. 
  From now, repositories should be added manually. For example:
  ```kotlin
  repositories {
      mavenCentral()
      papermc()
  }
  ```
- **Breaking change!**
  Don't set default `bukkit.apiVersion`.
  It was implicitly set to `1.16.4` for the sake of simplicity, but in fact it was unobvious behavior.
  Now, `bukkit.apiVersion` should be set explicitly:
  ```kotlin
  bukkit {
      apiVersion = "1.20.5"
  }
  ```
- Support setting "api-version" containing a patch version for v1.20.5+
- Add dependency substitution rules fixing paper groupId and substituting bukkit version.
  In version catalogs placeholder value `{bukkit.version}` can be used, and it will be replaced with the actual version:
  ```toml
  [libraries]
  paperApi = { module = "io.papermc.paper:paper-api", version = "{bukkit.apiVersion}" }
  ```
- Set the default [JVM toolchain](https://docs.gradle.org/current/userguide/toolchains.html) version
  instead of setting JVM target and source compatibility to 1.8.
  By default, the minimal supported JVM version compatible with the specified `bukkit.apiVersion` is used.
- Accept EULA using CLI parameter `-Dcom.mojang.eula.agree=true` instead of changing `eula.txt`

### Changed

- Change the default value of `bukkit.server.debug` to `false`.
  It is recommended to use IDE facilities to run server with enabled debugging.
- Change API for disabling `plugin.yml` generation:
  ```diff
  -bukkit.disableMetaGeneration()
  +bukkit.generatePluginYaml.set(false)
  ```
- Use lazy API for `bukkit.apiVersion` property.

### Fixed

- Fix PaperMC repository URL
- Fix group ID for PaperMC 1.17+
- Fix compatibility with new versions of Shadow plugin

### Housekeeping

- Target JVM 1.8 → 17
- Gradle 7.2 → 8.13
- Remove old deprecated APIs

## [0.10.1] - 2021-11-07

### Added

- Plugin meta now supports field `libraries`.

### Housekeeping

- Gradle 7.1.1 -> 7.2

## [0.10.0] - 2021-07-20

### Added

- Plugin should be compatible with [configuration cache](https://docs.gradle.org/current/userguide/configuration_cache.html)

### Changed

- JCenter replaced with MavenCentral
- Improved compatibility with Groovy
- Removed usages of deprecated APIs

### Fixed

- Add duplicate strategy to processResources (#58)
- Fix spigot core copying (#55)

## [0.9.2] - 2021-01-25

### Fixed
- Fixed task `copyPlugins` when shadow plugin is enabled

## [0.9.1] - 2021-01-16

### Added
- `codemc()` repository extension

### Changed
- **BREAKING CHANGE**: `bukkit` dependency extension renamed to `bukkitApi`.
  This name is more consistent, also it avoids conflict with `BukkitExtension` extension name.

## [0.9.0] - 2021-01-08

### Reworked tasks hierarchy

All plugin's tasks reworked to use actual Gradle APIs:
- [Task Configuration Avoidance][tca].
  Plugin's tasks will be created and configured only when it needed
- [Up-to-date checks][uptodate].
  Tasks will not re-run if input data not changed
- Optimized work in offline mode.

### Property syntax to configure meta fields
Use `.set` instead of `=`:
```diff
bukkit {
    meta {
-        desctiption = "My plugin's description"
+        description.set("My plugin's description")
    }
}
```

### Improved Bukkit version management

Field `bukkit.version` is deprecated now, you should use `bukkit.apiVersion` instead.
Also, you can specify a version for dev server different from `apiVersion`:
```kotlin
bukkit {
    apiVersion = "1.16.4"
    server {
        version = "1.15.2" // Want to test plugin on older minecraft version 
    }
}
```
If `bukkit.server.version` is not specified, will be used `bukkit.apiVersion` for server.

Now, BukkitGradle adds `api-version` field to `plugin.yml`.
It will be parsed from `bukkit.apiVersion` but you can override it with `bukkit.meta.apiVersion` if need:
```kotlin
bukkit {
    apiVersion = "1.16.4" // Inferred api-version is 1.16

    meta {
        apiVersion.set("1.13") // But here you can override it
    }
}
```

### Smarter plugin.yml generation

BukkitGradle will not "eat" your existing plugin.yml file.
Its content will be used if you've not configured `bukkit.meta` in BukkitGradle.

If you don't want plugin.yml generation at all, you can disable it:
```kotlin
bukkit {
    disableMetaGeneration()
}
```

### Re-written in Kotlin
The plugin has been converted to Kotlin to make support easier.
The plugin still can be configured with Groovy DSL but
now it is friendly to Kotlin DSL.

### Removed
- Removed task `:rebuildServerCore`, use `:buildServerCore --rerun-tasks` instead
- Removed extension `DependencyHandler.craftbukkit()`, use `DependencyHandler.spigot()` instead
- Removed automatic `mavenLocal()` apply, you should apply it manually if you need it
- Removed repository extension `RepositoryHandler.vault()`, use `RepositoryHandler.jitpack()` instead and read [VaultAPI README][vault]

### Changed
- `bukkit.run` renamed to `bukkit.server`. The old name is deprecated
- Add `nogui` argument by default to `bukkitArgs`
- Type of properties `server.jvmArgs` and `server.bukkitArgs` changed from `String` to `List<String>`.
  It makes it easier to add arguments without overriding defaults
- Default main class pattern changed from `<groupId>.<lowercased name>.<name>` to `<groupId>.<name>`

### Housekeeping
- Default bukkit version now is 1.16.4
- Update Gradle to 6.7.1

[tca]: https://docs.gradle.org/current/userguide/task_configuration_avoidance.html
[uptodate]: https://docs.gradle.org/current/userguide/more_about_tasks.html#sec:up_to_date_checks
[vault]: https://github.com/MilkBowl/VaultAPI

[unreleased]: https://github.com/EndlessCodeGroup/BukkitGradle/compare/1.0.0...develop
[1.0.0]: https://github.com/EndlessCodeGroup/BukkitGradle/compare/0.10.1...1.0.0
[0.10.1]: https://github.com/EndlessCodeGroup/BukkitGradle/compare/0.10.0...0.10.1
[0.10.0]: https://github.com/EndlessCodeGroup/BukkitGradle/compare/0.9.2...0.10.0
[0.9.2]: https://github.com/EndlessCodeGroup/BukkitGradle/compare/0.9.1...0.9.2
[0.9.1]: https://github.com/EndlessCodeGroup/BukkitGradle/compare/0.9.0...0.9.1
[0.9.0]: https://github.com/EndlessCodeGroup/BukkitGradle/compare/0.8.2...0.9.0
