package ru.endlesscode.gradle.bukkit.meta

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

class PluginMetaPluginTest {
    private File metaFile

    @Before
    void setUp() throws Exception {
        this.metaFile = getTestMetaFile()
    }

    private static File getTestMetaFile() {
        File metaFile = new File("build/tmp/bukkitPluginMetadata/test-meta.yml")
        metaFile.mkdirs()

        if (metaFile.exists()) {
            metaFile.delete()
        }

        metaFile << '''name: Default Name
description: Default description
version: 1.0

main: com.example.Plugin
author: OsipXD
website: www.example.com

prefix: ChosenOne'''

        return metaFile
    }

    @Test
    void testRemovingMetaLines() throws Exception {
        PluginMetaPlugin.removeAllMeta(this.metaFile)

        assertEquals(["prefix: ChosenOne"], this.metaFile.readLines())
    }
}