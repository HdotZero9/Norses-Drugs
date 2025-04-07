package com.drugs;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ToleranceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "Your drug tolerance levels:");

        Set<String> allDrugs = new TreeSet<>(DrugRegistry.getRegisteredDrugNames());

        for (String drug : allDrugs) {
            int level = ToleranceTracker.getToleranceLevel(player, drug);
            int max = ToleranceConfigLoader.getMaxTolerance(drug);
            double effectiveness = ToleranceConfigLoader.getEffectivenessMultiplier(drug, level) * 100;

            ChatColor color = (level == 0) ? ChatColor.GREEN
                    : (level < max) ? ChatColor.GOLD
                    : ChatColor.RED;

            sender.sendMessage(color + "- " + drug + ": " + level + "/" + max + " (" + (int) effectiveness + "% potency)");
        }

        return true;
    }
}
