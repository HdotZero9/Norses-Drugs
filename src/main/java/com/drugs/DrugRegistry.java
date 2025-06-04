package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads all drug definitions and recipes from config files.
 * Provides access to drug profiles and item matching.
 */
public class DrugRegistry {

    // Stores all loaded drug profiles, keyed by their ID
    private static final Map<String, DrugEffectProfile> drugProfiles = new ConcurrentHashMap<>();
    
    // Cache for item matching to avoid expensive lookups
    private static final Map<Integer, DrugEffectProfile> itemMatchCache = new ConcurrentHashMap<>();
    
    // Maximum cache size to prevent memory leaks
    private static final int MAX_CACHE_SIZE = 1000;
    
    // Flag to track if we need to clear the cache due to size
    private static boolean needsCacheCleanup = false;

    /**
     * Initializes all drug profiles and registers their recipes.
     *
     * @param plugin The plugin instance (for loading configs and namespaced keys)
     */
    public static void init(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        FileConfiguration recipes = plugin instanceof DrugsV2 dp ? dp.getRecipesConfig() : null;

        // Clear existing data
        drugProfiles.clear();
        itemMatchCache.clear();
        needsCacheCleanup = false;

        // Load drugs in parallel for large configs
        List<String> keys = new ArrayList<>(config.getKeys(false));
        
        // Process in batches for better performance
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
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
        
        Bukkit.getLogger().info("[DrugsV2] Loaded " + drugProfiles.size() + " drugs");
    }

    /**
     * Retrieves a profile by internal drug ID.
     */
    public static DrugEffectProfile getProfileById(String id) {
        return id == null ? null : drugProfiles.get(id.toLowerCase());
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
     * Uses caching for performance.
     */
    public static DrugEffectProfile getProfileFromItem(ItemStack item) {
        if (item == null) return null;
        
        // Generate a cache key based on item properties
        int cacheKey = generateItemCacheKey(item);
        
        // Check cache first
        DrugEffectProfile cachedProfile = itemMatchCache.get(cacheKey);
        if (cachedProfile != null) {
            return cachedProfile;
        }
        
        // If cache is too large, schedule cleanup
        if (itemMatchCache.size() > MAX_CACHE_SIZE && !needsCacheCleanup) {
            needsCacheCleanup = true;
            scheduleItemCacheCleanup();
        }
        
        // Perform the actual matching
        for (DrugEffectProfile profile : drugProfiles.values()) {
            if (profile.matches(item)) {
                // Cache the result
                itemMatchCache.put(cacheKey, profile);
                return profile;
            }
        }
        
        // Cache negative result to avoid repeated lookups
        itemMatchCache.put(cacheKey, null);
        return null;
    }
    
    /**
     * Generates a cache key for an item based on its properties
     */
    private static int generateItemCacheKey(ItemStack item) {
        if (item == null) return 0;
        
        int result = 1;
        result = 31 * result + item.getType().hashCode();
        
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            result = 31 * result + item.getItemMeta().getDisplayName().hashCode();
        }
        
        return result;
    }
    
    /**
     * Schedules a cleanup of the item match cache
     */
    private static void scheduleItemCacheCleanup() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(DrugsV2.getInstance(), () -> {
            Bukkit.getLogger().info("[DrugsV2] Cleaning item match cache (" + itemMatchCache.size() + " entries)");
            itemMatchCache.clear();
            needsCacheCleanup = false;
        }, 100L); // 5 seconds later
    }

    /**
     * Lists all registered drug IDs (for GUI or command use).
     */
    public static Set<String> getRegisteredDrugNames() {
        return new HashSet<>(drugProfiles.keySet()); // Return a copy to prevent modification
    }
    
    /**
     * Gets the number of registered drugs
     */
    public static int getDrugCount() {
        return drugProfiles.size();
    }
}
