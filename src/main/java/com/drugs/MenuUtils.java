package com.drugs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

/**
 * Utility methods for creating GUI items like buttons.
 */
public class MenuUtils {

    /**
     * Creates a simple item with a name and no lore.
     *
     * @param name     Display name with color codes
     * @param material Item type
     * @return A configured ItemStack
     */
    public static ItemStack createButtonItem(String name, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(Collections.singletonList(" "));
        item.setItemMeta(meta);

        return item;
    }
}
