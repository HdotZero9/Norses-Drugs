package com.drugs;

import org.bukkit.Bukkit;
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

        // Load default configs
        saveDefaultConfig();
        saveRecipesConfig();
        saveToleranceConfig();

        // Load tolerance settings
        ToleranceConfigLoader.load(getDataFolder());

        // Initialize core drug system
        DrugRegistry.init(this);

        // Start tolerance decay
        ToleranceTracker.startDecayTask();

        // Register events
        getServer().getPluginManager().registerEvents(new DrugMenuListener(), this);
        getServer().getPluginManager().registerEvents(new DrugUseListener(), this);
        Bukkit.getPluginManager().registerEvents(new AchievementsGUI(), DrugsV2.getInstance());


        // Register commands
        getCommand("drugs").setExecutor(new DrugsCommand());
        getCommand("drugs").setTabCompleter(new DrugsTabCompleter());
        getCommand("tolerance").setExecutor(new ToleranceCommand());

        getLogger().info("DrugsV2 enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DrugsV2 disabled.");
    }

    public void saveRecipesConfig() {
        if (recipesFile == null) {
            recipesFile = new File(getDataFolder(), "recipes.yml");
        }
        if (!recipesFile.exists()) {
            saveResource("recipes.yml", false);
        }
        recipesConfig = YamlConfiguration.loadConfiguration(recipesFile);
    }

    public void saveToleranceConfig() {
        File file = new File(getDataFolder(), "tolerance.yml");
        if (!file.exists()) {
            saveResource("tolerance.yml", false);
        }
    }

    public FileConfiguration getRecipesConfig() {
        return recipesConfig;
    }
}
