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

### Re-written in Kotlin
The plugin has been converted to Kotlin to make support easier.
The plugin still can be configured with Groovy DSL but
now it is friendly to Kotlin DSL.

### Added
- Configuration avoidance and build cache for task `generateMetaData`
