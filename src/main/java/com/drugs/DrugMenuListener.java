package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Handles clicks and closes for the Drugs GUI system.
 * Prevents interaction with both drug menu and recipe viewer.
 */
public class DrugMenuListener implements Listener {

    /**
     * Detect clicks inside the Drugs Menu or Recipe GUI.
     */
    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        ItemStack clicked = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if (inv == null || clicked == null || clicked.getType() == Material.AIR) return;

        String title = ChatColor.stripColor(event.getView().getTitle());
        if (!title.startsWith("Drugs Menu") && !title.startsWith("Recipe: ")) return;

        event.setCancelled(true); // Fully prevent item movement/dragging

        // Only handle logic for drug menu clicks
        if (!title.startsWith("Drugs Menu")) return;

        String name = clicked.getItemMeta() != null ? ChatColor.stripColor(clicked.getItemMeta().getDisplayName()) : "";

        switch (name.toLowerCase()) {
            case "next page" -> {
                int currentPage = parsePageNumber(title);
                DrugMenuGUI.open(player, currentPage + 1);
            }
            case "previous page" -> {
                int currentPage = parsePageNumber(title);
                DrugMenuGUI.open(player, currentPage - 1);
            }
            case "close" -> player.closeInventory();
            default -> {
                DrugEffectProfile profile = DrugRegistry.getProfileFromItem(clicked);
                if (profile != null) {
                    DrugRecipeViewer.openRecipe(player, profile.getName());
                }
            }
        }
    }

    /**
     * When closing a recipe GUI, return to the drug menu after a tick.
     */
    @EventHandler
    public void onRecipeClose(InventoryCloseEvent event) {
        String title = ChatColor.stripColor(event.getView().getTitle());
        if (title.startsWith("Recipe: ")) {
            Player player = (Player) event.getPlayer();
            Bukkit.getScheduler().runTaskLater(DrugsV2.getInstance(), () -> {
                DrugMenuGUI.open(player, 0);
            }, 1L); // Delay to prevent recursion
        }
    }

    /**
     * Extracts the page number from the GUI title.
     */
    private int parsePageNumber(String title) {
        try {
            return Integer.parseInt(title.replaceAll("\\D+", "")) - 1;
        } catch (Exception e) {
            return 0;
        }
    }
}
