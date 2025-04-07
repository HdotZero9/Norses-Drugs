package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerAchievementData {

    private final UUID playerUUID;
    private final File file;
    private final YamlConfiguration config;
    private static final File folder = new File(Bukkit.getPluginManager().getPlugin("DrugsV2").getDataFolder(), "data/achievements");

    public PlayerAchievementData(UUID uuid) {
        this.playerUUID = uuid;

        if (!folder.exists()) folder.mkdirs();

        this.file = new File(folder, uuid.toString() + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().severe("[DrugsV2] Failed to create achievement file for " + uuid);
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean hasAchievement(String key) {
        return config.getBoolean(key, false);
    }

    public void grantAchievement(String key) {
        if (!hasAchievement(key)) {
            config.set(key, true);
            save();
        }
    }

    public Set<String> getUnlockedAchievements() {
        Set<String> unlocked = new HashSet<>();
        for (String key : config.getKeys(false)) {
            if (config.getBoolean(key)) {
                unlocked.add(key);
            }
        }
        return unlocked;
    }

    public void resetAllAchievements() {
        for (String key : config.getKeys(false)) {
            config.set(key, false);
        }
        save();
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().severe("[DrugsV2] Failed to save achievement file for " + playerUUID);
            e.printStackTrace();
        }
    }
}
