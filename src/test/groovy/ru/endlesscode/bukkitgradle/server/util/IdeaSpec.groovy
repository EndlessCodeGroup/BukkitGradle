package ru.endlesscode.bukkitgradle.server.util

import spock.lang.Specification

class IdeaSpec extends Specification {

    def "test sanitizeFileName"(String name, String slug) {
        when:
        def nameSlug = Idea.sanitizeFileName(name)

        then:
        nameSlug == slug

        where:
        name                     | slug
        'Run Server'             | 'Run Server'
        'my-plugin: Run Server'  | 'my-plugin_ Run Server'
        'Run Server [my-plugin]' | 'Run Server [my-plugin]'
    }
}
