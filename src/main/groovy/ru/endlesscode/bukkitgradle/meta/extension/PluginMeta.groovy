package ru.endlesscode.bukkitgradle.meta.extension

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

@SuppressWarnings("unused")
class PluginMeta {
    private final MetaItem name = new MetaItem("name")
    private final MetaItem description = new MetaItem("description")
    private final MetaItem main = new MetaItem("main")
    private final MetaItem version = new MetaItem("version")
    private final MetaItem url = new MetaItem("website")
    private final MetaItem authors = new MetaItem("authors")

    @Internal
    final List<MetaItem> items = [name, description, main, version, url, authors]

    void setName(name) {
        this.name.value = name
    }

    @Input
    String getName() {
        return this.name.value
    }

    void setDescription(description) {
        this.description.value = description
    }

    @Optional
    @Input
    String getDescription() {
        return this.description.value
    }

    void setMain(main) {
        this.main.value = main
    }

    @Input
    String getMain() {
        return this.main.value
    }

    void setVersion(version) {
        this.version.value = version
    }

    @Input
    String getVersion() {
        return this.version.value
    }

    void setUrl(url) {
        this.url.value = url
    }

    @Optional
    @Input
    String getUrl() {
        return this.url.value
    }

    void setAuthors(authors) {
        this.authors.value = authors
    }

    @Optional
    @Input
    String getAuthors() {
        return this.authors.value
    }

    /**
     * Enclose value in single quotes.
     *
     * @param value The value
     * @return Value with single quotes around
     */
    static String q(value) {
        return "'$value'"
    }

    /**
     * Enclose value in double quotes.
     *
     * @param value The value
     * @return Value with double quotes around
     */
    static String qq(value) {
        return "\"$value\""
    }
}
