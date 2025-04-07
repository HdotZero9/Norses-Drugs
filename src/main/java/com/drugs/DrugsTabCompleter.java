package com.drugs;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides tab completion for /drugs command.
 */
public class DrugsTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        // /drugs <...>
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            if ("give".startsWith(args[0].toLowerCase())) options.add("give");
            return options;
        }

        // /drugs give <player>
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(p -> p.getName())
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        // /drugs give <player> <drugId>
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            Set<String> drugNames = DrugRegistry.getRegisteredDrugNames();
            return drugNames.stream()
                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        // /drugs give <player> <drugId> <amount>
        if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
            return Collections.singletonList("1");
        }

        return Collections.emptyList();
    }
}
