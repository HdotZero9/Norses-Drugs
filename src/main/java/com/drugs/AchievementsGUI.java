package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class AchievementsGUI implements Listener {

    private static final String GUI_TITLE = ChatColor.DARK_GREEN + "Drug Achievements";

    public static void open(Player player) {
        // If achievements are disabled, inform the player and return
        if (!AchievementManager.isEnabled()) {
            player.sendMessage(ChatColor.RED + "Achievements are disabled on this server.");
            return;
        }
        
        // Get all achievements and filter out disabled ones
        List<CustomAchievement> allAchievements = CustomAchievementLoader.getAllAchievements()
                .stream()
                .filter(CustomAchievement::isEnabled)
                .collect(Collectors.toList());
        
        // If all achievements are disabled, inform the player
        if (allAchievements.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No achievements are currently enabled.");
            return;
        }
        
        Set<String> unlocked = AchievementManager.getUnlocked(player);

        // Calculate inventory size (multiple of 9, with enough slots)
        int size = Math.min(54, ((allAchievements.size() + 8) / 9) * 9);
        Inventory gui = Bukkit.createInventory(null, size, GUI_TITLE);

        for (int i = 0; i < allAchievements.size(); i++) {
            CustomAchievement ach = allAchievements.get(i);
            boolean isUnlocked = unlocked.contains(ach.getId());

            // Use the configured icons
            Material material = isUnlocked ? ach.getCompletedIcon() : ach.getIcon();
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ach.getTitle()));
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.translateAlternateColorCodes('&', ach.getDescription()));
                lore.add("");
                lore.add(isUnlocked ? ChatColor.GREEN + "✔ Unlocked" : ChatColor.GRAY + "✖ Locked");
                meta.setLore(lore);
                item.setItemMeta(meta);
            }

            gui.setItem(i, item);
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getView().getTitle().equals(GUI_TITLE)) {
            event.setCancelled(true); // Prevent item stealing
        }
    }
}
