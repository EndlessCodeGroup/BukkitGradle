package ru.endlesscode.gradle.bukkit.meta

import org.gradle.api.Project

class PluginMeta {
    final MetaItem name
    final MetaItem description
    final MetaItem main
    final MetaItem version
    final MetaItem url
    final MetaItem authors

    private final List<MetaItem> metaItems = []

    PluginMeta(Project project) {
        this.name = new MetaItem('name', true, { project.name })
        this.description = new MetaItem('description', { project.description })
        this.main = new MetaItem('main', true)
        this.version = new MetaItem('version', true, { project.version })
        this.url = new MetaItem('website', { project.findProperty('url') })
        this.authors = new MetaItem('authors')

        metaItems.addAll(name, description, main, version, url, authors)
    }

    void setName(def name) {
        this.name.value = name
    }

    String getName() {
        return this.name.value
    }

    void setDescription(def description) {
        this.description.value = description
    }

    String getDescription() {
        return this.description.value
    }

    void setMain(def main) {
        this.main.value = main
    }

    String getMain() {
        return this.main.value
    }

    void setVersion(def version) {
        this.version.value = version
    }

    String getVersion() {
        return this.version.value
    }

    void setUrl(def url) {
        this.url.value = url
    }

    String getUrl() {
        return this.url.value
    }

    void setAuthors(List<String> authors) {
        this.authors.value = authors
    }

    String getAuthors() {
        return this.authors.value
    }

    List<MetaItem> getItems() {
        return metaItems
    }

    static String resolve(Object obj) {
        if (obj == null) {
            return null
        }

        if (obj instanceof String) {
            return obj
        }

        if (obj instanceof Closure) {
            return resolve(obj.call())
        }

        if (obj instanceof Class) {
            return obj.name
        }

        return obj as String
    }
}
