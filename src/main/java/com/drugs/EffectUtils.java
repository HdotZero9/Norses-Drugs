package com.drugs;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for parsing potion effects from config sections.
 */
public class EffectUtils {

    /**
     * Parses a list of potion effects from a configuration section.
     * Expected format:
     *   effects:
     *     SPEED:
     *       duration: 200
     *       amplifier: 1
     *     REGENERATION:
     *       duration: 100
     *       amplifier: 0
     *
     * @param section The section containing potion definitions
     * @return A list of parsed PotionEffects
     */
    public static List<PotionEffect> parsePotionEffects(ConfigurationSection section) {
        List<PotionEffect> effects = new ArrayList<>();
        if (section == null) return effects;

        for (String key : section.getKeys(false)) {
            PotionEffectType type = PotionEffectType.getByName(key.toUpperCase());
            if (type == null) continue;

            int duration = section.getInt(key + ".duration", 200);
            int amplifier = section.getInt(key + ".amplifier", 0);
            effects.add(new PotionEffect(type, duration, amplifier));
        }

        return effects;
    }
}
