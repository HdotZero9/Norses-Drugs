package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
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
            Bukkit.getLogger().info("[DrugsV2] Loading drug config: " + key);

            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) {
                Bukkit.getLogger().warning("[DrugsV2] Skipping '" + key + "': config section is null.");
                continue;
            }

            try {
                String name = key;
                Material material = Material.valueOf(section.getString("material", "SUGAR").toUpperCase());
                String displayName = section.getString("display-name", "&fUnknown Drug");
                List<String> lore = section.getStringList("lore");
                List<PotionEffect> effects = EffectUtils.parsePotionEffects(section.getConfigurationSection("effects"));

                DrugEffectProfile profile = new DrugEffectProfile(name, effects, material, displayName, lore);
                drugProfiles.put(key.toLowerCase(), profile);
                Bukkit.getLogger().info("[DrugsV2] Registered drug: " + key);

                // Register crafting recipe if it exists
                if (recipes != null) {
                    ConfigurationSection recipeSection = recipes.getConfigurationSection(key);
                    if (recipeSection != null) {
                        Bukkit.getLogger().info("[DrugsV2] Registering recipe for: " + key);
                        DrugRecipeHelper.registerDrugRecipe(key, recipeSection, plugin);
                    } else {
                        Bukkit.getLogger().warning("[DrugsV2] No recipe found for: " + key);
                    }
                }

            } catch (Exception e) {
                Bukkit.getLogger().severe("[DrugsV2] Failed to load drug '" + key + "': " + e.getMessage());
                e.printStackTrace();
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
     * Creates a drug item with specified amount.
     */
    public static ItemStack getDrugItem(String id, int amount) {
        DrugEffectProfile profile = getProfileById(id);
        if (profile == null) return null;
        return profile.createItem(amount);
    }

    /**
     * Tries to match a held item to a known drug.
     */
    public static DrugEffectProfile getProfileFromItem(ItemStack item) {
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
