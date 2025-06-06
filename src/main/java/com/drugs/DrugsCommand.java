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

        // /drugs achievements
        if (args[0].equalsIgnoreCase("achievements")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Only players can view achievements.");
                return true;
            }

            if (!player.hasPermission("drugs.achievements")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to view achievements.");
                return true;
            }

            AchievementsGUI.open(player);
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
            if (sender.hasPermission("drugs.achievements")) {
                sender.sendMessage(ChatColor.YELLOW + "/drugs achievements" + ChatColor.GRAY + " - View your achievement progress");
            }
            if (sender.hasPermission("drugs.admin.achievements")) {
                sender.sendMessage(ChatColor.YELLOW + "/drugs achievements toggle" + ChatColor.GRAY + " - Enable/disable the achievement system");
            }
            if (sender.hasPermission("drugs.admin.overdose")) {
                sender.sendMessage(ChatColor.YELLOW + "/drugs overdose reload" + ChatColor.GRAY + " - Reload overdose configuration");
                sender.sendMessage(ChatColor.YELLOW + "/drugs overdose reset <player>" + ChatColor.GRAY + " - Reset a player's overdose counts");
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
            OverdoseEffectManager.resetOverdoseCounts(target);
            sender.sendMessage(ChatColor.GREEN + "Reset all tolerance and overdose counts for " + target.getName());
            target.sendMessage(ChatColor.YELLOW + "Your drug tolerance levels and overdose counts have been purged by an admin.");
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
            DrugsV2.getInstance().saveAchievementSettingsConfig();
            DrugsV2.getInstance().saveAchievementsConfig();
            DrugsV2.getInstance().saveOverdoseConfig();

            ToleranceConfigLoader.load(DrugsV2.getInstance().getDataFolder());
            AchievementSettingsLoader.load(DrugsV2.getInstance().getDataFolder());
            CustomAchievementLoader.load(DrugsV2.getInstance().getDataFolder());
            OverdoseEffectManager.load(DrugsV2.getInstance().getDataFolder());
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
        
        // /drugs achievements toggle
        if (args[0].equalsIgnoreCase("achievements") && args.length > 1 && args[1].equalsIgnoreCase("toggle")) {
            if (!sender.hasPermission("drugs.admin.achievements")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to toggle achievements.");
                return true;
            }
            
            // We'd need to modify the config file directly to toggle achievements
            // For now, just inform the user to edit the config file
            sender.sendMessage(ChatColor.YELLOW + "To enable/disable achievements, edit the achievement_settings.yml file");
            sender.sendMessage(ChatColor.YELLOW + "and set 'enabled' to true or false, then use /drugs reload.");
            return true;
        }
        
        // /drugs overdose reload
        if (args[0].equalsIgnoreCase("overdose")) {
            if (!sender.hasPermission("drugs.admin.overdose")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to manage overdose settings.");
                return true;
            }
            
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /drugs overdose [reload/reset]");
                return true;
            }
            
            // /drugs overdose reload
            if (args[1].equalsIgnoreCase("reload")) {
                OverdoseEffectManager.load(DrugsV2.getInstance().getDataFolder());
                sender.sendMessage(ChatColor.GREEN + "Overdose configuration reloaded successfully.");
                return true;
            }
            
            // /drugs overdose reset <player>
            if (args[1].equalsIgnoreCase("reset")) {
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /drugs overdose reset <player>");
                    return true;
                }
                
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found or not online.");
                    return true;
                }
                
                OverdoseEffectManager.resetOverdoseCounts(target);
                sender.sendMessage(ChatColor.GREEN + "Reset all overdose counts for " + target.getName());
                target.sendMessage(ChatColor.YELLOW + "Your overdose counts have been reset by an admin.");
                return true;
            }
            
            sender.sendMessage(ChatColor.RED + "Unknown overdose subcommand. Try reload or reset.");
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Unknown subcommand. Try /drugs help");
        return true;
    }
}
