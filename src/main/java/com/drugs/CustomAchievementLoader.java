package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads custom achievements from the achievements.yml file
 */
public class CustomAchievementLoader {

    private static final Map<String, CustomAchievement> achievements = new HashMap<>();
    private static final Map<String, List<CustomAchievement>> triggerMap = new HashMap<>();
    
    /**
     * Loads all achievements from the config file
     * 
     * @param dataFolder The plugin data folder
     */
    public static void load(File dataFolder) {
        File file = new File(dataFolder, "achievements.yml");
        
        // Create default file if it doesn't exist
        if (!file.exists()) {
            DrugsV2.getInstance().saveResource("achievements.yml", false);
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        // Clear existing achievements
        achievements.clear();
        triggerMap.clear();
        
        // Load each achievement section
        for (String key : config.getKeys(false)) {
            // Skip comment sections that start with #
            if (key.startsWith("#")) continue;
            
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) continue;
            
            try {
                String title = section.getString("title", "&7Unnamed Achievement");
                String description = section.getString("description", "&8No description");
                String trigger = section.getString("trigger", "unknown");
                
                // Parse material icons with fallbacks
                Material icon = Material.GRAY_DYE;
                Material completedIcon = Material.BOOK;
                
                try {
                    String iconStr = section.getString("icon", "GRAY_DYE");
                    icon = Material.valueOf(iconStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("[DrugsV2] Invalid icon material for achievement: " + key);
                }
                
                try {
                    String completedIconStr = section.getString("completed_icon", "BOOK");
                    completedIcon = Material.valueOf(completedIconStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("[DrugsV2] Invalid completed_icon material for achievement: " + key);
                }
                
                // Create the achievement
                CustomAchievement achievement = new CustomAchievement(
                    key, title, description, trigger, icon, completedIcon
                );
                
                // Register the achievement
                achievements.put(key, achievement);
                
                // Add to trigger map for quick lookup
                triggerMap.computeIfAbsent(trigger, k -> new ArrayList<>()).add(achievement);
                
                Bukkit.getLogger().info("[DrugsV2] Loaded achievement: " + key + " (" + trigger + ")");
                
            } catch (Exception e) {
                Bukkit.getLogger().severe("[DrugsV2] Failed to load achievement: " + key);
                e.printStackTrace();
            }
        }
        
        Bukkit.getLogger().info("[DrugsV2] Loaded " + achievements.size() + " achievements");
    }
    
    /**
     * Gets all loaded achievements
     */
    public static List<CustomAchievement> getAllAchievements() {
        return new ArrayList<>(achievements.values());
    }
    
    /**
     * Gets an achievement by its ID
     */
    public static CustomAchievement getAchievement(String id) {
        return achievements.get(id);
    }
    
    /**
     * Gets all achievements with a specific trigger
     */
    public static List<CustomAchievement> getAchievementsByTrigger(String trigger) {
        return triggerMap.getOrDefault(trigger, new ArrayList<>());
    }
} 