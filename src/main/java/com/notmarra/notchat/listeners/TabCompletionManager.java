package com.notmarra.notchat.listeners;

import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import com.notmarra.notchat.NotChat;

public class TabCompletionManager implements Listener {
    
    private final NotChat plugin;
    
    public TabCompletionManager(NotChat plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerCommandSendEvent(PlayerCommandSendEvent event) {
        event.getCommands().clear();

        
        for (String commandName : plugin.getKnownCommands().keySet()) {
            Command command = plugin.getKnownCommands().get(commandName);
            if (command.getPermission() == null || event.getPlayer().hasPermission(command.getPermission())) {
                event.getCommands().add(commandName);
                event.getCommands().addAll(command.getAliases());
            }
        }
    }
}
