package com.drugs;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DrugsPlaceholderExpansion extends PlaceholderExpansion {
    private final DrugsV2 plugin;

    public DrugsPlaceholderExpansion(DrugsV2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "drugs";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null || !player.isOnline()) {
            return "";
        }

        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) {
            return "";
        }

        // Handle tolerance placeholders: %drugs_<drugname>%
        String drugId = params.toLowerCase();
        if (DrugRegistry.isDrugRegistered(drugId)) {
            int tolerance = ToleranceTracker.getToleranceLevel(onlinePlayer, drugId);
            return String.valueOf(tolerance);
        }

        // Handle max tolerance placeholders: %drugs_<drugname>_max%
        if (params.toLowerCase().endsWith("_max")) {
            String drug = params.substring(0, params.length() - 4).toLowerCase();
            if (DrugRegistry.isDrugRegistered(drug)) {
                int maxTolerance = ToleranceConfigLoader.getMaxTolerance(drug);
                return String.valueOf(maxTolerance);
            }
        }

        // Handle effectiveness placeholders: %drugs_<drugname>_effectiveness%
        if (params.toLowerCase().endsWith("_effectiveness")) {
            String drug = params.substring(0, params.length() - 13).toLowerCase();
            if (DrugRegistry.isDrugRegistered(drug)) {
                int tolerance = ToleranceTracker.getToleranceLevel(onlinePlayer, drug);
                double effectiveness = ToleranceConfigLoader.getEffectivenessMultiplier(drug, tolerance) * 100;
                return String.format("%.1f", effectiveness);
            }
        }

        return null;
    }
} 