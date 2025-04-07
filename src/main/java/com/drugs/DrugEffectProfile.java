package com.drugs;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collections;
import java.util.List;

/**
 * Represents the core definition of a drug item.
 * Includes all metadata needed for item creation, effects, and future systems like tolerance or side effects.
 */
public class DrugEffectProfile {

    private final String name;
    private final List<PotionEffect> effects;
    private final Material material;
    private final String displayName;
    private final List<String> lore;

    /**
     * Constructs a new DrugEffectProfile.
     *
     * @param name        Internal ID of the drug (e.g. "heroin", "cane")
     * @param effects     List of positive potion effects to apply
     * @param material    Base item type (e.g. SUGAR, GHAST_TEAR)
     * @param displayName Display name with color codes
     * @param lore        Lore lines (formatted)
     */
    public DrugEffectProfile(String name, List<PotionEffect> effects, Material material, String displayName, List<String> lore) {
        this.name = name;
        this.effects = effects != null ? effects : Collections.emptyList();
        this.material = material;
        this.displayName = displayName;
        this.lore = lore != null ? lore : Collections.emptyList();
    }

    public String getName() {
        return name;
    }

    public List<PotionEffect> getEffects() {
        return effects;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    /**
     * Applies all defined effects to the given player.
     *
     * @param player Player to apply effects to
     */
    public void applyEffects(Player player) {
        for (PotionEffect effect : effects) {
            player.addPotionEffect(effect);
        }
    }

    /**
     * Checks whether a given item matches this drug profile.
     * Used for right-click detection.
     */
    public boolean matches(ItemStack item) {
        return DrugItemBuilder.matchesProfile(item, this);
    }

    /**
     * Creates an ItemStack representing this drug.
     *
     * @param amount Amount of the item to create
     * @return A configured ItemStack
     */
    public ItemStack createItem(int amount) {
        return DrugItemBuilder.buildDrugItem(this, amount);
    }
}
