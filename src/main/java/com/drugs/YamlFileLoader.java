package com.drugs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

/**
 * Utility for loading external YAML config files like recipes.yml
 */
public class YamlFileLoader {

    /**
     * Loads a YAML file from the plugin's data folder.
     *
     * @param filename The name of the file to load (e.g. "recipes.yml")
     * @param plugin   The plugin instance
     * @return The loaded FileConfiguration
     */
    public static FileConfiguration load(String filename, Plugin plugin) {
        File file = new File(plugin.getDataFolder(), filename);
        if (!file.exists()) {
            plugin.saveResource(filename, false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}
