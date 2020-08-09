package ru.endlesscode.bukkitgradle.server.util

import spock.lang.Specification

class IdeaSpec extends Specification {

    def "test fileNameSlug"(String name, String slug) {
        when:
        def nameSlug = Idea.fileNameSlug(name)

        then:
        nameSlug == slug

        where:
        name                    | slug
        'Run Server'            | 'Run_Server'
        'my-plugin: Run Server' | 'my_plugin__Run_Server'
    }
}
