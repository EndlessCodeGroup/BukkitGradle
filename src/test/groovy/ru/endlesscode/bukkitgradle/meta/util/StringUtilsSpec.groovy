package ru.endlesscode.bukkitgradle.meta.util

import spock.lang.Specification

class StringUtilsSpec extends Specification {

    void 'when toPascalCase - should return camel cased value'() {
        expect:
        StringUtils.toPascalCase(value) == expectedResult

        where:
        value                         | expectedResult
        "some string"                 | "SomeString"
        "another string_with-symbols" | "AnotherStringWithSymbols"
        ""                            | ""
    }
}
