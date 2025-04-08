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

public class AchievementsGUI implements Listener {

    private static final String GUI_TITLE = ChatColor.DARK_GREEN + "Drug Achievements";

    public static void open(Player player) {
        List<AchievementManager.DrugAchievement> allAchievements = AchievementManager.DrugAchievement.getAll();
        Set<String> unlocked = AchievementManager.getUnlocked(player);

        int size = 9; // 1 row for now
        Inventory gui = Bukkit.createInventory(null, size, GUI_TITLE);

        for (int i = 0; i < allAchievements.size(); i++) {
            AchievementManager.DrugAchievement ach = allAchievements.get(i);
            boolean isUnlocked = unlocked.contains(ach.key);

            ItemStack item = new ItemStack(isUnlocked ? Material.BOOK : Material.GRAY_DYE);
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ach.title));
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.translateAlternateColorCodes('&', ach.description));
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
