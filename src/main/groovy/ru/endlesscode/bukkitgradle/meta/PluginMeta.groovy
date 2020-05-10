package ru.endlesscode.bukkitgradle.meta

import org.gradle.api.Project

@SuppressWarnings("unused")
class PluginMeta {
    private final MetaItem name = new MetaItem("name", true)
    private final MetaItem description = new MetaItem("description")
    private final MetaItem main = new MetaItem("main", true)
    private final MetaItem version = new MetaItem("version", true)
    private final MetaItem url = new MetaItem("website")
    private final MetaItem authors = new MetaItem("authors")

    final List<MetaItem> items = [name, description, main, version, url, authors]

    PluginMeta() {
        // Nothing by default
    }

    PluginMeta(Project project) {
        setName(project.name)
        setDescription({ project.description })
        setMain({ "${project.group}.${getName().toLowerCase()}.${getName()}" })
        setVersion({ project.version })
        setUrl({ project.findProperty("url") })
    }

    void setName(name) {
        this.name.value = name
    }

    String getName() {
        return this.name.value
    }

    void setDescription(description) {
        this.description.value = description
    }

    String getDescription() {
        return this.description.value
    }

    void setMain(main) {
        this.main.value = main
    }

    String getMain() {
        return this.main.value
    }

    void setVersion(version) {
        this.version.value = version
    }

    String getVersion() {
        return this.version.value
    }

    void setUrl(url) {
        this.url.value = url
    }

    String getUrl() {
        return this.url.value
    }

    void setAuthors(authors) {
        this.authors.value = authors
    }

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
