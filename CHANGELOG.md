## Unreleased

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

### run -> server
`bukkit.run` renamed to `bukkit.server`. The old name is deprecated

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

### Re-written in Kotlin
The plugin has been converted to Kotlin to make support easier.
The plugin still can be configured with Groovy DSL but
now it is friendly to Kotlin DSL.

### Added
- Configuration avoidance and build cache for task `generateMetaData`

### Removed
- Task `:rebuildServerCore`, use `:buildServerCore --rerun-tasks` instead
- Extension `DependencyHandler.craftbukkit()`, use `DependencyHandler.spigot()` instead
- Automatic `mavenLocal()` apply, you should apply it manually if you need it

### Housekeeping
- Default bukkit version now is 1.16.4
- Update Gradle to 6.7.1

[tca]: https://docs.gradle.org/current/userguide/task_configuration_avoidance.html
[uptodate]: https://docs.gradle.org/current/userguide/more_about_tasks.html#sec:up_to_date_checks
