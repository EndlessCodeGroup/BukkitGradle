package ru.endlesscode.bukkitgradle

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class PluginSpecification extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile
    File settingsFile

    def setup() {
        buildFile = file('build.gradle')
        settingsFile = file('settings.gradle')

        //language=gradle
        buildFile << """
            plugins {
                id 'ru.endlesscode.bukkitgradle'
            }

            version = '1.0'
            group = 'com.example.testplugin'
        """.stripIndent()

        settingsFile << '''
            rootProject.name = 'test-plugin'
        '''.stripIndent()
    }

    File file(String path) {
        def file = new File(testProjectDir.root, path)
        if (!file.exists()) {
            file.parentFile.mkdirs()
            return testProjectDir.newFile(path)
        }
        return file
    }

    GradleRunner getRunner() {
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .forwardOutput()
                .withPluginClasspath()
    }
}
