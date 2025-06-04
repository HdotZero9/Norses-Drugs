package com.drugs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DrugsTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1) {
            List<String> options = new ArrayList<>();

            options.add("give");
            options.add("help");
            options.add("list");
            options.add("purge");
            options.add("reload");
            options.add("achievements"); // ✅ added this!
            
            if (sender.hasPermission("drugs.admin.overdose")) {
                options.add("overdose");
            }

            return partialMatch(args[0], options);
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("achievements")) {
            if (sender.hasPermission("drugs.admin.achievements")) {
                return partialMatch(args[1], Collections.singletonList("toggle"));
            }
            return Collections.emptyList();
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("overdose")) {
            if (sender.hasPermission("drugs.admin.overdose")) {
                return partialMatch(args[1], Arrays.asList("reload", "reset"));
            }
            return Collections.emptyList();
        }
        
        if (args.length == 3 && args[0].equalsIgnoreCase("overdose") && args[1].equalsIgnoreCase("reset")) {
            if (sender.hasPermission("drugs.admin.overdose")) {
                List<String> playerNames = new ArrayList<>();
                for (Player online : sender.getServer().getOnlinePlayers()) {
                    playerNames.add(online.getName());
                }
                return partialMatch(args[2], playerNames);
            }
            return Collections.emptyList();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            List<String> playerNames = new ArrayList<>();
            for (Player online : sender.getServer().getOnlinePlayers()) {
                playerNames.add(online.getName());
            }
            return partialMatch(args[1], playerNames);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            List<String> drugIds = new ArrayList<>(DrugRegistry.getRegisteredDrugNames());
            return partialMatch(args[2], drugIds);
        }

        return Collections.emptyList();
    }

    private List<String> partialMatch(String input, List<String> options) {
        List<String> result = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(input.toLowerCase())) {
                result.add(option);
            }
        }
        return result;
    }
}
