package com.notmarra.notchat.listeners;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // Add only commands that the player has permission for
            if (player.hasPermission("some.permission")) {
                completions.add("somecommand");
            }
        }
        return completions;
    }
}
