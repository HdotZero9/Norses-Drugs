package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;


/**
 * Loads and registers shaped crafting recipes from recipes.yml
 */
public class DrugRecipeHelper {

    /**
     * Registers a shaped crafting recipe from config for a given drug.
     *
     * Expected format in recipes.yml:
     *   drug_id:
     *     shape:
     *       - " A "
     *       - "BCB"
     *       - " D "
     *     ingredients:
     *       A: SUGAR
     *       B: BONE
     *       C: GHAST_TEAR
     *       D: APPLE
     *
     * @param id     The internal drug ID (e.g. "heroin")
     * @param section The section for this drug in recipes.yml
     * @param plugin The plugin instance for key registration
     */
    public static void registerDrugRecipe(String id, ConfigurationSection section, Plugin plugin) {
        if (section == null) return;

        Bukkit.getLogger().info("[DrugsV2] Attempting to register recipe for: " + id);

        // Parse the shape
        var shape = section.getStringList("shape");
        if (shape.size() != 3) return;

        // Build the result item
        DrugEffectProfile profile = DrugRegistry.getProfileById(id);
        if (profile == null) return;

        ItemStack result = profile.createItem(1);
        NamespacedKey key = new NamespacedKey(plugin, id.toLowerCase());

        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(shape.toArray(new String[0]));

        // Set each character as a material
        ConfigurationSection ingredients = section.getConfigurationSection("ingredients");
        if (ingredients == null) return;

        for (String symbol : ingredients.getKeys(false)) {
            String matName = ingredients.getString(symbol);
            Material mat = Material.matchMaterial(matName);
            if (mat != null) {
                recipe.setIngredient(symbol.charAt(0), mat);
            }
        }

        Bukkit.addRecipe(recipe);
    }
}
