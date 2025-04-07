package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AchievementsGUI {

    public static void open(Player player) {
        List<AchievementManager.DrugAchievement> allAchievements = AchievementManager.DrugAchievement.getAll();
        Set<String> unlocked = AchievementManager.getUnlocked(player);

        int size = 9; // We'll keep it 1 row (9 slots) for now
        Inventory gui = Bukkit.createInventory(null, size, ChatColor.DARK_GREEN + "Drug Achievements");

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
}
