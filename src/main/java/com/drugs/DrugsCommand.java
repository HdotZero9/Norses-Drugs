package com.drugs;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the /drugs command to open the GUI.
 */
public class DrugsCommand implements CommandExecutor {

    /**
     * Called when a player runs /drugs
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Must be a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        // Open the drug menu (page 0)
        DrugMenuGUI.open(player, 0);
        return true;
    }
}
