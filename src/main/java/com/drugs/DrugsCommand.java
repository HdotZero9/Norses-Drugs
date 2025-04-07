package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Handles /drugs and /drugs give
 */
public class DrugsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /drugs = open GUI
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Only players can open the GUI.");
                return true;
            }
            DrugMenuGUI.open(player, 0);
            return true;
        }

        // /drugs give <player> <drug> [amount]
        if (args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("drugs.give")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /drugs give <player> <drugId> [amount]");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            String drugId = args[2];
            int amount = 1;
            if (args.length >= 4) {
                try {
                    amount = Integer.parseInt(args[3]);
                    if (amount <= 0) amount = 1;
                } catch (NumberFormatException ignored) {
                    sender.sendMessage(ChatColor.RED + "Invalid amount, defaulting to 1.");
                }
            }

            ItemStack item = DrugRegistry.getDrugItem(drugId, amount);
            if (item == null) {
                sender.sendMessage(ChatColor.RED + "Drug '" + drugId + "' not found.");
                return true;
            }

            target.getInventory().addItem(item);
            sender.sendMessage(ChatColor.GREEN + "Gave " + amount + "x " + drugId + " to " + target.getName());
            target.sendMessage(ChatColor.GOLD + "You received " + amount + "x " + drugId + " from " + sender.getName());
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Unknown subcommand. Try /drugs or /drugs give");
        return true;
    }
}
