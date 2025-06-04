package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class OverdoseTracker {

    public static void handleOveruse(Player player, String drugId) {
        UUID uuid = player.getUniqueId();

        if (!ToleranceTracker.isAtMaxTolerance(uuid, drugId)) {
            return;
        }

        int count = ToleranceTracker.incrementOverdoseCount(player, drugId);
        
        // Process overdose with the new effect manager
        boolean shouldDie = OverdoseEffectManager.processOverdose(player, drugId, count);
        
        // If no custom effects were applied and the manager says to kill the player
        if (shouldDie) {
            // Grant achievement BEFORE killing
            Map<String, Object> context = AchievementManager.createContext();
            context.put("drug_id", drugId);
            AchievementManager.processTrigger(player, "overdose_death", context);

            // Delay actual death to ensure everything completes
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setHealth(0);
                }
            }.runTaskLater(DrugsV2.getInstance(), 1L);
        } else if (count < 3) {
            player.sendMessage(ChatColor.RED + "You're too tolerant to feel anything...");
        }
    }
}
