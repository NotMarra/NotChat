package com.notmarra.notchat.listeners.chat.modules;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandSendEvent;

import com.notmarra.notchat.NotChat;
import com.notmarra.notchat.listeners.NotChatListener;

public class TabChatListener extends NotChatListener {
    public static final String ID = "tab";

    private Map<String, Command> knownCommands = new HashMap<>();

    public TabChatListener(NotChat plugin) { super(plugin); }

    @Override
    public String getId() { return ID; }

    @EventHandler
    public void onPlayerCommandSendEvent(PlayerCommandSendEvent event) {
        event.getCommands().clear();

        for (String name : knownCommands.keySet()) {
            Command command = knownCommands.get(name);
            String permission = command.getPermission();

            if (permission == null || event.getPlayer().hasPermission(permission)) {
                event.getCommands().add(name);
                event.getCommands().addAll(command.getAliases());
            }
        }
    }

    @Override
    public void register() {
        super.register();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            this.knownCommands = getServer().getCommandMap().getKnownCommands();
        }, 20L);
    }
}
