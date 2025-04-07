package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a drug's data and how it behaves.
 */
public class DrugEffectProfile {

    private final String name;
    private final List<PotionEffect> effects;
    private final Material material;
    private final String displayName;
    private final List<String> lore;

    public DrugEffectProfile(String name, List<PotionEffect> effects, Material material, String displayName, List<String> lore) {
        this.name = name;
        this.effects = effects;
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
    }

    /**
     * Applies the drug effects to the player, scaling with tolerance.
     */
    public void applyEffects(Player player) {
        int toleranceLevel = ToleranceTracker.getToleranceLevel(player, name);
        int max = ToleranceConfigLoader.getMaxTolerance(name);
        double multiplier = ToleranceTracker.getEffectivenessMultiplier(player, name);

        // Maxed tolerance logic
        if (toleranceLevel >= max) {
            ToleranceTracker.onDrugUse(player, name); // consume, track use

            if (ToleranceTracker.incrementOverdoseCount(player, name) >= 3) {
                player.setHealth(0.0);
                Bukkit.broadcastMessage(ChatColor.RED + player.getName() + " died of a drug overdose.");
            } else {
                player.sendMessage(ChatColor.RED + "You're too tolerant to feel anything.");
            }

            return;
        }

        // Apply scaled effects
        for (PotionEffect baseEffect : effects) {
            int newDuration = (int) (baseEffect.getDuration() * multiplier);
            int amplifier = baseEffect.getAmplifier();

            if (newDuration > 0) {
                player.addPotionEffect(new PotionEffect(
                        baseEffect.getType(),
                        newDuration,
                        amplifier,
                        baseEffect.isAmbient(),
                        baseEffect.hasParticles(),
                        baseEffect.hasIcon()
                ));
            }
        }

        ToleranceTracker.onDrugUse(player, name);
    }

    public ItemStack createItem(int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            List<String> formattedLore = new ArrayList<>();
            for (String line : lore) {
                formattedLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(formattedLore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean matches(ItemStack item) {
        if (item == null || item.getType() != material || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return false;

        String itemName = ChatColor.stripColor(meta.getDisplayName());
        String expectedName = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', displayName));
        return itemName.equalsIgnoreCase(expectedName);
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
}
