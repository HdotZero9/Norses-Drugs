package com.drugs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Main plugin class for DrugsV2.
 */
public class DrugsV2 extends JavaPlugin {

    private static DrugsV2 instance;
    private FileConfiguration recipesConfig;
    private File recipesFile;

    public static DrugsV2 getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Save default configs
        saveDefaultConfig();
        saveRecipesConfig();

        // Initialize all core systems
        DrugRegistry.init(this);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new DrugMenuListener(), this);
        getServer().getPluginManager().registerEvents(new DrugUseListener(), this);

        // Register commands + tab completion
        getCommand("drugs").setExecutor(new DrugsCommand());
        getCommand("drugs").setTabCompleter(new DrugsTabCompleter());

        getLogger().info("DrugsV2 enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DrugsV2 disabled.");
    }

    /**
     * Loads or creates recipes.yml.
     */
    public void saveRecipesConfig() {
        if (recipesFile == null) {
            recipesFile = new File(getDataFolder(), "recipes.yml");
        }
        if (!recipesFile.exists()) {
            saveResource("recipes.yml", false);
        }
        recipesConfig = YamlConfiguration.loadConfiguration(recipesFile);
    }

    /**
     * Accessor for recipes.yml data.
     */
    public FileConfiguration getRecipesConfig() {
        return recipesConfig;
    }
}
