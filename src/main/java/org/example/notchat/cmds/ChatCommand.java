package org.example.notchat.cmds;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.notchat.utils.ChatGroup;
import org.example.notchat.utils.ChatManager;

public class ChatCommand implements CommandExecutor {
    private final ChatManager chatManager;

    public ChatCommand(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Tento příkaz může použít pouze hráč!");
            return true;
        }

        if (args.length < 1) {
            showHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "join" -> handleJoin(player, args);
            case "list" -> handleList(player);
            default -> showHelp(player);
        }

        return true;
    }

    private void handleJoin(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Použití: /chat join <skupina>").color(NamedTextColor.RED));
            return;
        }

        ChatGroup group = chatManager.getGroup(args[1]);
        if (group == null) {
            player.sendMessage(Component.text("Tato skupina neexistuje!").color(NamedTextColor.RED));
            return;
        }

        if (!group.hasPermission(player)) {
            player.sendMessage(Component.text("Nemáte oprávnění vstoupit do této skupiny!").color(NamedTextColor.RED));
            return;
        }

        chatManager.setPlayerGroup(player, group.getName());
        player.sendMessage(Component.text("Připojili jste se do chat skupiny: " + group.getName()).color(NamedTextColor.GREEN));
    }

    private void handleList(Player player) {
        player.sendMessage(Component.text("Dostupné chat skupiny:").color(NamedTextColor.GOLD));
        for (ChatGroup group : chatManager.getAllGroups()) {
            if (group.hasPermission(player)) {
                player.sendMessage(Component.text("- " + group.getName()).color(NamedTextColor.YELLOW));
            }
        }
    }

    private void showHelp(Player player) {
        player.sendMessage(Component.text("Chat Plugin - Nápověda").color(NamedTextColor.GOLD));
        player.sendMessage(Component.text("/chat join <skupina> - Připojit se do chat skupiny").color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/chat list - Zobrazit seznam dostupných skupin").color(NamedTextColor.YELLOW));
    }
}
