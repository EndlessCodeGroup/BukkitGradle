package ru.endlesscode.bukkitgradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class PluginSpecification extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile
    File settingsFile

    protected Project project
    protected GradleRunner runner
    protected BuildResult result

    def setup() {
        buildFile = file('build.gradle')
        settingsFile = file('settings.gradle')

        //language=gradle
        buildFile << '''
            plugins {
                id 'ru.endlesscode.bukkitgradle'
            }

            version = '1.0'
            group = 'com.example.testplugin'
        '''.stripIndent()

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

    File dir(String path) {
        def file = new File(testProjectDir.root, path)
        if (!file.exists()) {
            file.parentFile.mkdirs()
            return testProjectDir.newFolder(path)
        }
        return file
    }

    Project getProject() {
        if (project == null) {
            project = ProjectBuilder.builder()
                    .withProjectDir(testProjectDir.root)
                    .build()
        }
        return project
    }

    GradleRunner getRunner() {
        if (runner == null) {
            runner = GradleRunner.create()
                    .withProjectDir(testProjectDir.root)
                    .forwardOutput()
                    .withPluginClasspath()
        }
        return runner
    }

    protected def run(String... args) {
        result = getRunner().withArguments(args.toList()).build()
    }

    protected TaskOutcome taskOutcome(String task) {
        return result.task(task).outcome
    }
}
