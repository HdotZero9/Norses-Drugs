package com.drugs;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Responsible for creating ItemStacks for drugs and matching items to drug profiles.
 * Ensures drug items have correct name, lore, and material.
 */
public class DrugItemBuilder {

    /**
     * Creates a drug item based on a DrugEffectProfile.
     *
     * @param profile The drug profile to generate the item from
     * @param amount  How many items to create
     * @return A configured ItemStack
     */
    public static ItemStack buildDrugItem(DrugEffectProfile profile, int amount) {
        ItemStack item = new ItemStack(profile.getMaterial(), amount);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', profile.getDisplayName()));
        meta.setLore(profile.getLore().stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .toList());

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Checks whether an in-hand item matches the given drug profile.
     * Used for drug use detection on right-click.
     *
     * @param item    The item in the player’s hand
     * @param profile The drug profile to match against
     * @return True if the item matches this profile
     */
    public static boolean matchesProfile(ItemStack item, DrugEffectProfile profile) {
        if (item == null || item.getType() != profile.getMaterial()) return false;
        if (!item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return false;

        String rawName = ChatColor.stripColor(meta.getDisplayName());
        String profileName = ChatColor.stripColor(
                ChatColor.translateAlternateColorCodes('&', profile.getDisplayName()));

        return rawName.equalsIgnoreCase(profileName);
    }
}
