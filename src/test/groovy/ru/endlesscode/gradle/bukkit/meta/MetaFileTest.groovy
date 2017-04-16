package ru.endlesscode.gradle.bukkit.meta

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import ru.endlesscode.gradle.bukkit.BukkitPlugin

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static org.junit.Assert.assertEquals

class MetaFileTest {
    private static final Path PROJECT_DIR = Paths.get("build/tmp/bukkitPluginMetadata")

    private Project project
    private MetaFile metaFile
    private Path target

    @Before
    void setUp() throws Exception {
        Files.createDirectories(PROJECT_DIR)
        this.project = ProjectBuilder.builder().build()
        this.project.with {
            apply plugin: BukkitPlugin
        }

        this.target = createMetaFile()
        this.metaFile = new MetaFile(this.project, this.target)
    }

    private static Path createMetaFile() {
        Path metaFile = PROJECT_DIR.resolve(MetaFile.NAME)
        Files.deleteIfExists(metaFile)
        Files.createFile(metaFile)

        metaFile << '''name: Default Name
description: Default description
version: 1.0

main: com.example.Plugin
author: OsipXD
website: www.example.com

depend: [Vault, ProtocolLib]'''
    }

    @Test
    void testRemovingMetaLines() throws Exception {
        assertEquals(["depend: [Vault, ProtocolLib]"], this.target.readLines())
    }

    @Test
    void testGeneratingMeta() throws Exception {
        project.with {
            bukkit.meta {
                name = "Name"
                description = "Description"
                version = "1.0"
                main = "com.example.Plugin"
                authors = ["OsipXD"]
                url = "www.example.com"
            }
        }

        List<String> expected = [
                "name: Name",
                "description: Description",
                "main: com.example.Plugin",
                "version: 1.0",
                "website: www.example.com",
                "authors: [OsipXD]",
                "depend: [Vault, ProtocolLib]"
        ]

        this.metaFile.writeTo(target)
        assertEquals(expected, target.readLines())
    }

    @Test(expected = GradleException)
    void testMissingRequiredMetaMustThrowException() throws Exception {
        this.metaFile.writeTo(target)
    }
}