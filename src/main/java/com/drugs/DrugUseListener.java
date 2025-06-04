package com.drugs;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DrugUseListener implements Listener {

    // Track drug use count per player
    private static final Map<String, Integer> drugUseCount = new HashMap<>();

    @EventHandler
    public void onDrugUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        DrugEffectProfile profile = DrugRegistry.getProfileFromItem(item);
        if (profile == null) return;

        event.setCancelled(true);

        // Get the drug ID
        String drugId = DrugRegistry.getRegisteredDrugNames().stream()
                .filter(id -> DrugRegistry.getProfileById(id).matches(item))
                .findFirst()
                .orElse(null);

        if (drugId == null) return;

        // ----------------------------------------
        // Track drug use count for achievements
        // ----------------------------------------
        String playerKey = player.getUniqueId().toString();
        int useCount = drugUseCount.getOrDefault(playerKey, 0) + 1;
        drugUseCount.put(playerKey, useCount);

        // ----------------------------------------
        // Process achievement triggers
        // ----------------------------------------
        
        // First drug use achievement
        Map<String, Object> firstUseContext = AchievementManager.createContext();
        AchievementManager.processTrigger(player, "first_drug_use", firstUseContext);
        
        // Specific drug use achievement
        Map<String, Object> specificDrugContext = AchievementManager.createContext();
        specificDrugContext.put("drug_id", drugId);
        AchievementManager.processTrigger(player, "use_specific_drug", specificDrugContext);
        
        // Use count achievement
        Map<String, Object> countContext = AchievementManager.createContext();
        countContext.put("count", useCount);
        AchievementManager.processTrigger(player, "use_count", countContext);
        
        // Track for connoisseur achievement (all drugs used)
        trackDrugUsedForConnoisseur(player, drugId);
        
        // Max tolerance achievement
        if (ToleranceTracker.isAtMaxTolerance(player.getUniqueId(), drugId)) {
            Map<String, Object> maxToleranceContext = AchievementManager.createContext();
            AchievementManager.processTrigger(player, "use_at_max", maxToleranceContext);
        }

        // ----------------------------------------
        // 🧪 Apply effects
        // ----------------------------------------
        profile.applyEffects(player);

        if (player.getGameMode() != GameMode.CREATIVE) {
            int newAmount = item.getAmount() - 1;
            if (newAmount <= 0) {
                player.getInventory().setItemInMainHand(null);
            } else {
                item.setAmount(newAmount);
            }
        }
    }
    
    /**
     * Tracks drug usage for the connoisseur achievement
     */
    private void trackDrugUsedForConnoisseur(Player player, String drugId) {
        PlayerAchievementData data = new PlayerAchievementData(player.getUniqueId());
        String perDrugKey = "connoisseur-used-" + drugId;

        if (!data.hasAchievement(perDrugKey)) {
            data.grantAchievement(perDrugKey);

            Set<String> required = new HashSet<>();
            for (String id : DrugRegistry.getRegisteredDrugNames()) {
                required.add("connoisseur-used-" + id);
            }

            if (data.getUnlockedAchievements().containsAll(required)) {
                Map<String, Object> context = AchievementManager.createContext();
                AchievementManager.processTrigger(player, "all_drugs_used", context);
            }
        }
    }
}
