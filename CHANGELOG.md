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

### Added
- Configuration avoidance and build cache for task `generateMetaData`
