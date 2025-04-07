package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for DrugsV2.
 * Handles loading configs, initializing systems, and registering commands/listeners.
 */
public class DrugsV2 extends JavaPlugin {

    private static DrugsV2 instance;

    @Override
    public void onEnable() {
        instance = this;

        // Save configs if missing
        saveDefaultConfig();
        saveResource("recipes.yml", false);

        // Load all drugs
        DrugRegistry.init(this);

        // Register event listeners
        Bukkit.getPluginManager().registerEvents(new DrugUseListener(), this);
        Bukkit.getPluginManager().registerEvents(new DrugMenuListener(), this);

        // Register /drugs command
        getCommand("drugs").setExecutor(new DrugsCommand());

        getLogger().info("DrugsV2 enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DrugsV2 disabled.");
    }

    public static DrugsV2 getInstance() {
        return instance;
    }

    public FileConfiguration getRecipesConfig() {
        return YamlFileLoader.load("recipes.yml", this);
    }
}
