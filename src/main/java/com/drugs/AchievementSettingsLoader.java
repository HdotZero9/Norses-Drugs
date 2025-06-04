package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads and provides access to achievement settings from achievement_settings.yml
 */
public class AchievementSettingsLoader {

    private static boolean enabled = true;
    private static boolean chatNotifications = true;
    private static boolean soundNotifications = true;
    private static boolean fireworkNotifications = true;
    private static String unlockPrefix = "&6[Drugs] &aAchievement Unlocked: ";
    
    private static final Map<String, AchievementSettings> achievementSettings = new HashMap<>();
    
    public static class AchievementSettings {
        private final boolean enabled;
        private final String title;
        private final String description;
        
        public AchievementSettings(boolean enabled, String title, String description) {
            this.enabled = enabled;
            this.title = title;
            this.description = description;
        }
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public String getTitle() {
            return title;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Loads achievement settings from the configuration file
     */
    public static void load(File dataFolder) {
        File file = new File(dataFolder, "achievement_settings.yml");
        
        // Create default file if it doesn't exist
        if (!file.exists()) {
            DrugsV2.getInstance().saveResource("achievement_settings.yml", false);
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        // Load main settings
        enabled = config.getBoolean("enabled", true);
        
        // Load notification settings
        ConfigurationSection notifSection = config.getConfigurationSection("notifications");
        if (notifSection != null) {
            chatNotifications = notifSection.getBoolean("chat_message", true);
            soundNotifications = notifSection.getBoolean("sound", true);
            fireworkNotifications = notifSection.getBoolean("fireworks", true);
        }
        
        // Load message settings
        ConfigurationSection msgSection = config.getConfigurationSection("messages");
        if (msgSection != null) {
            unlockPrefix = msgSection.getString("unlock_prefix", unlockPrefix);
        }
        
        // Load individual achievement settings
        ConfigurationSection achieveSection = config.getConfigurationSection("achievements");
        if (achieveSection != null) {
            for (String key : achieveSection.getKeys(false)) {
                ConfigurationSection section = achieveSection.getConfigurationSection(key);
                if (section != null) {
                    boolean achieveEnabled = section.getBoolean("enabled", true);
                    String title = section.getString("title", "");
                    String description = section.getString("description", "");
                    
                    achievementSettings.put(key, new AchievementSettings(achieveEnabled, title, description));
                }
            }
        }
        
        Bukkit.getLogger().info("[DrugsV2] Loaded achievement settings. System enabled: " + enabled);
    }
    
    /**
     * Checks if the achievement system is globally enabled
     */
    public static boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Checks if chat notifications are enabled
     */
    public static boolean isChatNotificationsEnabled() {
        return chatNotifications;
    }
    
    /**
     * Checks if sound notifications are enabled
     */
    public static boolean isSoundNotificationsEnabled() {
        return soundNotifications;
    }
    
    /**
     * Checks if firework notifications are enabled
     */
    public static boolean isFireworkNotificationsEnabled() {
        return fireworkNotifications;
    }
    
    /**
     * Gets the unlock message prefix
     */
    public static String getUnlockPrefix() {
        return unlockPrefix;
    }
    
    /**
     * Checks if a specific achievement is enabled
     */
    public static boolean isAchievementEnabled(String key) {
        if (!enabled) return false;
        AchievementSettings settings = achievementSettings.get(key);
        return settings != null && settings.isEnabled();
    }
    
    /**
     * Gets the title for an achievement
     */
    public static String getAchievementTitle(String key, String defaultTitle) {
        AchievementSettings settings = achievementSettings.get(key);
        return settings != null ? settings.getTitle() : defaultTitle;
    }
    
    /**
     * Gets the description for an achievement
     */
    public static String getAchievementDescription(String key, String defaultDesc) {
        AchievementSettings settings = achievementSettings.get(key);
        return settings != null ? settings.getDescription() : defaultDesc;
    }
} 