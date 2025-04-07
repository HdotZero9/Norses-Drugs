package com.drugs;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Handles player interactions with drug items.
 * Applies effects when right-clicking with a recognized drug in hand.
 */
public class DrugUseListener implements Listener {

    @EventHandler
    public void onDrugUse(PlayerInteractEvent event) {
        // Only listen for right-click in main hand
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Try to match the held item to a registered drug
        DrugEffectProfile profile = DrugRegistry.getProfileFromItem(item);
        if (profile == null) return;

        // Cancel the interaction so default behavior (eating, placing) doesn’t trigger
        event.setCancelled(true);

        // Apply the drug's configured effects
        profile.applyEffects(player);

        // Reduce item count by 1 if not in creative mode
        if (player.getGameMode() != org.bukkit.GameMode.CREATIVE) {
            int newAmount = item.getAmount() - 1;
            if (newAmount <= 0) {
                player.getInventory().setItemInMainHand(null);
            } else {
                item.setAmount(newAmount);
            }
        }
    }
}
