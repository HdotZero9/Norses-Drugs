package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Handles /drugs command and subcommands.
 */
public class DrugsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // /drugs
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Only players can open the drug menu.");
                return true;
            }
            DrugMenuGUI.open(player, 0);
            return true;
        }

        // /drugs help
        if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColor.GOLD + "-----[ DrugsV2 Help ]-----");
            sender.sendMessage(ChatColor.YELLOW + "/drugs" + ChatColor.GRAY + " - Open the drug selection GUI");
            if (sender.hasPermission("drugs.give")) {
                sender.sendMessage(ChatColor.YELLOW + "/drugs give <player> <drug> [amount]" + ChatColor.GRAY + " - Give a drug to someone");
            }
            if (sender.hasPermission("drugs.tolerance")) {
                sender.sendMessage(ChatColor.YELLOW + "/tolerance" + ChatColor.GRAY + " - View your current drug tolerance");
            }
            if (sender.hasPermission("drugs.admin.purge")) {
                sender.sendMessage(ChatColor.YELLOW + "/drugs purge <player>" + ChatColor.GRAY + " - Reset a player's tolerance");
            }
            if (sender.hasPermission("drugs.admin.reload")) {
                sender.sendMessage(ChatColor.YELLOW + "/drugs reload" + ChatColor.GRAY + " - Reload all plugin configs");
            }
            if (sender.hasPermission("drugs.admin.list")) {
                sender.sendMessage(ChatColor.YELLOW + "/drugs list" + ChatColor.GRAY + " - Show all available drugs");
            }
            sender.sendMessage(ChatColor.YELLOW + "/drugs help" + ChatColor.GRAY + " - Show this help menu");
            sender.sendMessage("");
            sender.sendMessage(ChatColor.DARK_RED + "⚠ Using drugs too often causes tolerance. Max tolerance = no effects!");
            sender.sendMessage(ChatColor.RED + "💀 Use drugs at max tolerance multiple times and you may die of overdose!");
            return true;
        }

        // /drugs give
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
                } catch (NumberFormatException e) {
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

        // /drugs purge <player>
        if (args[0].equalsIgnoreCase("purge")) {
            if (!sender.hasPermission("drugs.admin.purge")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to purge tolerance.");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /drugs purge <player>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found or not online.");
                return true;
            }

            ToleranceTracker.resetAllTolerance(target);
            sender.sendMessage(ChatColor.GREEN + "Reset all tolerance for " + target.getName());
            target.sendMessage(ChatColor.YELLOW + "Your drug tolerance levels have been purged by an admin.");
            return true;
        }

        // /drugs reload
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("drugs.admin.reload")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to reload the plugin.");
                return true;
            }

            DrugsV2.getInstance().reloadConfig();
            DrugsV2.getInstance().saveRecipesConfig();
            DrugsV2.getInstance().saveToleranceConfig();

            ToleranceConfigLoader.load(DrugsV2.getInstance().getDataFolder());
            DrugRegistry.init(DrugsV2.getInstance());

            sender.sendMessage(ChatColor.GREEN + "DrugsV2 configs reloaded successfully.");
            return true;
        }

        // /drugs list
        if (args[0].equalsIgnoreCase("list")) {
            if (!sender.hasPermission("drugs.admin.list")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to view the drug list.");
                return true;
            }

            sender.sendMessage(ChatColor.GOLD + "-----[ Registered Drugs ]-----");

            Set<String> all = new TreeSet<>(DrugRegistry.getRegisteredDrugNames());
            for (String drugId : all) {
                DrugEffectProfile profile = DrugRegistry.getProfileById(drugId);
                if (profile == null) continue;

                sender.sendMessage(ChatColor.YELLOW + "- " + ChatColor.translateAlternateColorCodes('&', profile.getDisplayName()));
                for (String lore : profile.getLore()) {
                    sender.sendMessage(ChatColor.GRAY + "  " + ChatColor.translateAlternateColorCodes('&', lore));
                }
            }
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Unknown subcommand. Try /drugs help");
        return true;
    }
}
