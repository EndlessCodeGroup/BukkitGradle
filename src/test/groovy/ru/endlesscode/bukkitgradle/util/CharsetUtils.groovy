package ru.endlesscode.bukkitgradle.util

import java.lang.reflect.Field
import java.nio.charset.Charset

class CharsetUtils {

    static void setDefaultCharset(String charset) {
        System.setProperty("file.encoding", charset)
        Field charsetField = Charset.getDeclaredField("defaultCharset")
        charsetField.setAccessible(true)
        charsetField.set(null, null)
    }
}
