package ru.endlesscode.bukkitgradle.util

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized)
class StringUtilsTest {

    private Object value
    private String expectedResult

    @Parameterized.Parameters
    static Collection<Object[]> data() {
        return [
                args(null, null),
                args({ null }, null),
                args("", ""),
                args("Some text", "Some text"),
                args(42, "42"),
                args({ 42 }, "42"),
                args({ "I'm lazy" }, "I'm lazy"),
                args(StringUtils, "ru.endlesscode.bukkitgradle.util.StringUtils")
        ]
    }

    private static Object[] args(def value, String expectedResult) {
        def array = new Object[2]
        array[0] = value
        array[1] = expectedResult
        return array
    }

    StringUtilsTest(def value, String expectedResult) {
        this.value = value
        this.expectedResult = expectedResult
    }

    @Test
    void 'when resolve value - should return expected result'() {
        // When
        def resolved = StringUtils.resolve(value)

        // Then
        assert expectedResult == resolved
    }
}
