package com.drugs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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

            return partialMatch(args[0], options);
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
