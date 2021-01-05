## Unreleased

### Property syntax to configure meta fields
Instead of:
```kotlin
bukkit {
    meta {
        desctiption = "My plugin's description"
    }
}
```
You should use:
```kotlin
bukkit {
    meta {
        description.set("My plugin's description")
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

### Housekeeping
- Default bukkit version now is 1.16.4
- Update Gradle to 6.7.1
