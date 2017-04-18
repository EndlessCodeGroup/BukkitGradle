package ru.endlesscode.bukkitgradle.server

import org.gradle.api.logging.Logger

class StreamSpy extends Thread {
    static void spy(InputStream input, Logger logger) {
        startDaemon {
            input.eachLine { line ->
                logger.lifecycle line
            }
        }
    }

    static void spy(InputStream input, OutputStream output) {
        startDaemon {
            def writer = output.newWriter()
            input.eachLine { line ->
                writer.write "${line}\n"
                writer.flush()
            }
        }
    }
}
