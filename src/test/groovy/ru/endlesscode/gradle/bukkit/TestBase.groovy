package ru.endlesscode.gradle.bukkit

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import ru.endlesscode.gradle.bukkit.meta.MetaFile

import java.nio.file.Files
import java.nio.file.Path

class TestBase {
    protected Project project

    @Before
    void setUp() throws Exception {
        this.project = ProjectBuilder.builder()
                .withName("TestProject")
                .withProjectDir(new File("build/testProject"))
                .build()
        this.project.with {
            apply plugin: BukkitPlugin

            group = "com.example.plugin"
            description = "Test project description"
            version = "1.0"
            ext.url = "https://www.example.ru/"
        }
    }

    protected void initBukkitMeta() {
        this.project.bukkit.meta {
            name = "TestPlugin"
            description = "Test plugin description"
            version = "0.1"
            main = "com.example.plugin.Plugin"
            authors = ["OsipXD", "Contributors"]
            url = "http://www.example.com/"
        }
    }

    protected Path createDefaultMetaFile() {
        Path metaDir = this.project.buildDir.toPath().resolve("meta/")
        Files.createDirectories(metaDir)
        Path metaFile = metaDir.resolve(MetaFile.NAME)
        Files.deleteIfExists(metaFile)
        Files.createFile(metaFile)

        metaFile << '''name: TestPlugin_s
description: Test plugin description_s
version: 0.1_s

main: com.example.plugin.Plugin_s
author: OsipXD
website: www.example_s.com

depend: [Vault, ProtocolLib]'''
    }
}
