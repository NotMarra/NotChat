package com.notmarra.notchat.listeners;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.notmarra.notchat.NotChat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TabComplete implements TabCompleter {

    // TODO: Test if working or its just breaking the entire server
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!NotChat.hasPAPI() || !(sender instanceof Player)) {
            return null;
        }
        
        Player player = (Player) sender;
        List<String> suggestions = new ArrayList<>();
        
        Map<String, Command> knownCommands = NotChat.getInstance().getServer().getCommandMap().getKnownCommands();
        
        for (Command cmd : knownCommands.values()) {
            String permission = cmd.getPermission();
            if (permission == null || NotChat.getPerms().has(player, permission)) {
                if (args.length == 0 || cmd.getName().startsWith(args[0])) {
                    suggestions.add(cmd.getName());
                }
                
                for (String cmdAlias : cmd.getAliases()) {
                    if (args.length == 0 || cmdAlias.startsWith(args[0])) {
                        suggestions.add(cmdAlias);
                    }
                }
            }
        }
        
        return suggestions;
    }
}
