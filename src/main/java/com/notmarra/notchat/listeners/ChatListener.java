package com.notmarra.notchat.listeners;

import com.notmarra.notchat.NotChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final NotChat plugin;

    public ChatListener(NotChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String format;

        if (player.hasPermission("notchat.owner")) {
            format = plugin.getConfig().getString("chat-formats.owner");
        } else if (player.hasPermission("notchat.vip")) {
            format = plugin.getConfig().getString("chat-formats.vip");
        } else {
            format = plugin.getConfig().getString("chat-formats.default");
        }

        format = format.replace("%player%", player.getName())
                .replace("%message%", event.getMessage());

        Component message = MiniMessage.miniMessage().deserialize(format);

        if (plugin.getConfig().getBoolean("local-chat.enabled")) {
            int radius = plugin.getConfig().getInt("local-chat.radius");
            for (Player p : player.getWorld().getPlayers()) {
                if (p.getLocation().distance(player.getLocation()) <= radius) {
                    p.sendMessage(message);
                }
            }
        } else {
            player.getServer().broadcast(message);
        }

        event.setCancelled(true);
    }
}