package com.drugs;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class DrugUseListener implements Listener {

    @EventHandler
    public void onDrugUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        DrugEffectProfile profile = DrugRegistry.getProfileFromItem(item);
        if (profile == null) return;

        event.setCancelled(true);

        // ----------------------------------------
        // ✅ Achievement: First Dose
        // ----------------------------------------
        AchievementManager.grant(player, AchievementManager.DrugAchievement.FIRST_DOSE);

        // ----------------------------------------
        // ✅ Achievement: Chem Connoisseur
        // ----------------------------------------
        String drugId = DrugRegistry.getRegisteredDrugNames().stream()
                .filter(id -> DrugRegistry.getProfileById(id).matches(item))
                .findFirst()
                .orElse(null);

        if (drugId != null) {
            PlayerAchievementData data = new PlayerAchievementData(player.getUniqueId());
            String perDrugKey = "connoisseur-used-" + drugId;

            if (!data.hasAchievement(perDrugKey)) {
                data.grantAchievement(perDrugKey);

                Set<String> required = new HashSet<>();
                for (String id : DrugRegistry.getRegisteredDrugNames()) {
                    required.add("connoisseur-used-" + id);
                }

                if (data.getUnlockedAchievements().containsAll(required)) {
                    AchievementManager.grant(player, AchievementManager.DrugAchievement.CONNOISSEUR);
                }
            }
        }

        // ----------------------------------------
        // ✅ Achievement: Risky Business
        // ----------------------------------------
        if (drugId != null && ToleranceTracker.isAtMaxTolerance(player.getUniqueId(), drugId)) {
            AchievementManager.grant(player, AchievementManager.DrugAchievement.RISKY_BUSINESS);
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
}
