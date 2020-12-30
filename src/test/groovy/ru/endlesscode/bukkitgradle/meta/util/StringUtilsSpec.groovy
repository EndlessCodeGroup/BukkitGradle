package ru.endlesscode.bukkitgradle.meta.util

import spock.lang.Specification

class StringUtilsSpec extends Specification {

    void 'when resolve value - should return expected result'() {
        expect:
        expectedResult == StringUtils.resolve(value)

        where:
        expectedResult                                      | value
        null                                                | null
        null                                                | { null }
        ""                                                  | ""
        "Some text"                                         | "Some text"
        "42"                                                | 42
        "42"                                                | { 42 }
        "I'm lazy"                                          | { "I'm lazy" }
        "ru.endlesscode.bukkitgradle.meta.util.StringUtils" | StringUtils
    }

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
