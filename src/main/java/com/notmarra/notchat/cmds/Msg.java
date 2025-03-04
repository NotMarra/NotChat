package com.notmarra.notchat.cmds;

import com.notmarra.notchat.NotChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Msg implements CommandExecutor {

    private final NotChat plugin;

    public Msg(NotChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            return false;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }

        target.sendMessage("§6[MSG from " + sender.getName() + "]§f: " + message);
        sender.sendMessage("§6[MSG to " + target.getName() + "]§f: " + message);

        return true;
    }
}