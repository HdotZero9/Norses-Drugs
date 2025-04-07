package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import java.util.*;

/**
 * Loads all drug definitions and recipes from config files.
 * Provides access to drug profiles and item matching.
 */
public class DrugRegistry {

    // Stores all loaded drug profiles, keyed by their ID
    private static final Map<String, DrugEffectProfile> drugProfiles = new HashMap<>();

    /**
     * Initializes all drug profiles and registers their recipes.
     *
     * @param plugin The plugin instance (for loading configs and namespaced keys)
     */
    public static void init(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        FileConfiguration recipes = plugin instanceof DrugsV2 dp ? dp.getRecipesConfig() : null;

        drugProfiles.clear();

        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) continue;

            // Parse drug profile
            String name = key;
            Material material = Material.valueOf(section.getString("material", "SUGAR").toUpperCase());
            String displayName = section.getString("display-name", "&fUnknown Drug");
            List<String> lore = section.getStringList("lore");
            List<PotionEffect> effects = EffectUtils.parsePotionEffects(section.getConfigurationSection("effects"));

            DrugEffectProfile profile = new DrugEffectProfile(name, effects, material, displayName, lore);
            drugProfiles.put(key.toLowerCase(), profile);

            // Register crafting recipe if it exists in recipes.yml
            if (recipes != null) {
                ConfigurationSection recipeSection = recipes.getConfigurationSection(key);
                if (recipeSection != null) {
                    Bukkit.getLogger().info("[DrugsV2] Attempting to register recipe for: " + key);
                    DrugRecipeHelper.registerDrugRecipe(key, recipeSection, plugin);
                    Bukkit.getLogger().info("[DrugsV2] Successfully registered recipe: " + key);
                }
            }
        }
    }

    /**
     * Retrieves a profile by internal drug ID.
     */
    public static DrugEffectProfile getProfileById(String id) {
        return drugProfiles.get(id.toLowerCase());
    }

    /**
     * Tries to match a held item to a known drug.
     */
    public static DrugEffectProfile getProfileFromItem(org.bukkit.inventory.ItemStack item) {
        for (DrugEffectProfile profile : drugProfiles.values()) {
            if (profile.matches(item)) return profile;
        }
        return null;
    }

    /**
     * Lists all registered drug IDs (for GUI or command use).
     */
    public static Set<String> getRegisteredDrugNames() {
        return drugProfiles.keySet();
    }
}
