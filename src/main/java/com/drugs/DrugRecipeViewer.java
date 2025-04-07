package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Displays a crafting table GUI that visually shows the recipe for a drug.
 */
public class DrugRecipeViewer {

    /**
     * Opens a visual crafting grid to show the recipe for the given drug ID.
     *
     * @param player The player viewing the recipe
     * @param drugId The internal ID of the drug
     */
    public static void openRecipe(Player player, String drugId) {
        Plugin plugin = DrugsV2.getInstance();
        ConfigurationSection section = plugin instanceof DrugsV2 dp ? dp.getRecipesConfig().getConfigurationSection(drugId) : null;
        if (section == null) {
            player.sendMessage("§cRecipe not found for drug: " + drugId);
            return;
        }

        // Create a crafting grid (InventoryType.WORKBENCH has a 3x3 layout)
        Inventory recipeGUI = Bukkit.createInventory(null, InventoryType.WORKBENCH, "Recipe: " + drugId);

        // Load shape and ingredients
        var shape = section.getStringList("shape");
        ConfigurationSection ingredients = section.getConfigurationSection("ingredients");

        if (shape.size() != 3 || ingredients == null) {
            player.sendMessage("§cInvalid recipe format.");
            return;
        }

        // Build ingredient map
        Map<Character, Material> ingredientMap = new HashMap<>();
        for (String key : ingredients.getKeys(false)) {
            Material mat = Material.matchMaterial(ingredients.getString(key));
            if (mat != null) {
                ingredientMap.put(key.charAt(0), mat);
            }
        }

        // Fill crafting grid (3x3 = slots 1–9 in WORKBENCH inventory layout)
        int index = 1;
        for (String row : shape) {
            for (char c : row.toCharArray()) {
                Material mat = ingredientMap.getOrDefault(c, Material.AIR);
                if (mat != Material.AIR) {
                    recipeGUI.setItem(index, new ItemStack(mat));
                }
                index++;
            }
        }

        player.openInventory(recipeGUI);
    }
}
