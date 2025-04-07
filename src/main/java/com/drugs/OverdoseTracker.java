package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class OverdoseTracker {

    public static void handleOveruse(Player player, String drugId) {
        UUID uuid = player.getUniqueId();

        if (!ToleranceTracker.isAtMaxTolerance(uuid, drugId)) {
            return;
        }

        int count = ToleranceTracker.incrementOverdoseCount(player, drugId);

        if (count >= 3) {
            // Grant achievement BEFORE killing

            // Broadcast overdose death
            Bukkit.broadcastMessage(ChatColor.RED + player.getName() + " died of a drug overdose.");

            // Delay actual death to ensure everything completes
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setHealth(0);
                }
            }.runTaskLater(DrugsV2.getInstance(), 1L);
        } else {
            player.sendMessage(ChatColor.RED + "You're too tolerant to feel anything...");
        }
    }
}
