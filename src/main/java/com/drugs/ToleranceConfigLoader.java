package com.drugs;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ToleranceConfigLoader {

    private static final Map<String, Integer> maxTolerance = new HashMap<>();
    private static final Map<String, Integer> decayMinutes = new HashMap<>();
    private static final Map<String, Map<Integer, Double>> scaling = new HashMap<>();

    public static void load(File dataFolder) {
        File file = new File(dataFolder, "tolerance.yml");
        if (!file.exists()) {
            DrugsV2.getInstance().saveResource("tolerance.yml", false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String drug : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(drug);
            if (section == null) continue;

            maxTolerance.put(drug.toLowerCase(), section.getInt("max-tolerance", 5));
            decayMinutes.put(drug.toLowerCase(), section.getInt("decay-delay-minutes", 3));

            Map<Integer, Double> levels = new HashMap<>();
            ConfigurationSection scale = section.getConfigurationSection("effectiveness-scaling");
            if (scale != null) {
                for (String key : scale.getKeys(false)) {
                    levels.put(Integer.parseInt(key), scale.getDouble(key));
                }
            }
            scaling.put(drug.toLowerCase(), levels);
        }
    }

    public static int getMaxTolerance(String drugId) {
        return maxTolerance.getOrDefault(drugId.toLowerCase(),
                maxTolerance.getOrDefault("default", 5));
    }

    public static int getDecayMinutes(String drugId) {
        return decayMinutes.getOrDefault(drugId.toLowerCase(),
                decayMinutes.getOrDefault("default", 3));
    }

    public static double getEffectivenessMultiplier(String drugId, int toleranceLevel) {
        Map<Integer, Double> map = scaling.getOrDefault(drugId.toLowerCase(), scaling.get("default"));
        return map.getOrDefault(toleranceLevel, 1.0);
    }
}
