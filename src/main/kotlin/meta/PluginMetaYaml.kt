package meta

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PluginMetaYaml(

    /** The class of the plugin that extends JavaPlugin. */
    val main: String? = null,

    /** Name of the plugin. */
    val name: String? = null,

    /** A human friendly description of the functionality the plugin provides. */
    val description: String? = null,

    /** The name to use when logging to console instead of the plugin's name. */
    val prefix: String? = null,

    /** The version of the plugin. */
    val version: String? = null,

    /** The version of the API that plugin use. */
    @SerialName("api-version")
    val apiVersion: String? = null,

    /** Explicitly state when a plugin should be loaded. */
    val load: String? = null,

    /** Uniquely identifies who developed this plugin. */
    val author: String? = null,

    /**
     * Allows to list multiple authors, if it is a collaborative project.
     * @see author
     */
    val authors: List<String>? = null,

    /** The plugin's or author's website. */
    val website: String? = null,

    /** A list of plugins that your plugin requires to load. */
    val depend: List<String>? = null,

    /** A list of plugins that are required for your plugin to have full functionality. */
    val softdepend: List<String>? = null,

    /** A list of plugins that should be loaded after your plugin. */
    val loadbefore: List<String>? = null,

    /** The name of a command the plugin wishes to register, as well as an optional list of command attributes. */
    val commands: Map<String, PluginCommand>? = null,

    /** Permission that the plugin wishes to register. */
    val permissions: Map<String, PluginPermission>? = null
) : java.io.Serializable

@Serializable
internal data class PluginCommand(

    /** A short description of what the command does. */
    val description: String? = null,

    /** Alternate command names a user may use instead. */
    val aliases: List<String>? = null,

    /** The most basic permission node required to use the command. */
    val permission: String? = null,

    /**
     * A no-permission message which is displayed to a user if they do not have the required permission
     * to use this command.
     */
    @SerialName("permission-message")
    val permissionMessage: String? = null,

    /** A short description of how to use this command. */
    val usage: String? = null
) : java.io.Serializable

@Serializable
internal data class PluginPermission(

    /** A short description of what this permission allows. */
    val description: String? = null,

    /** The default value of the permission. */
    val default: String? = null,

    /** Children for the permission. */
    val children: Map<String, Boolean>? = null
) : java.io.Serializable
