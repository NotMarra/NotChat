package com.notmarra.notchat.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandSendEvent;

import com.notmarra.notchat.NotChat;

public class TabListener extends BaseNotListener {
    public static final String ID = "tab";

    private Map<String, Command> knownCommands = new HashMap<>();

    public TabListener(NotChat plugin) {
        super(plugin);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean hasConfig() {
        return false;
    }

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
            this.knownCommands = plugin.getServer().getCommandMap().getKnownCommands();
        }, 20L);
    }
}
