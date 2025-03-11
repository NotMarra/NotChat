package com.notmarra.notchat.listeners;

import com.notmarra.notchat.NotChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Set;



public class ChatListener implements Listener {
    private final NotChat plugin;

    public ChatListener(NotChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        NotChat.getInstance().getLogger().info("Player sended message ");
        HashMap<String, String> chat_formats = NotChat.chat_formats;
        Set<String> keys = chat_formats.keySet();

        if (NotChat.hasVault()) {
            String group = NotChat.getPerms().getPrimaryGroup(player);

            if (chat_formats.containsKey(group)) {
                String format = chat_formats.get(group);
                sendMessage(player, format, event);
                return;
            }
        }

        for (String format : keys) {
            NotChat.getInstance().getLogger().info("Format: " + format);

            if (player.hasPermission("notchat." + format)) {
                sendMessage(player, format, event);
                return;
            }
        }

        sendMessage(player, "default", event);
    }

    private void sendMessage(Player player, String format, AsyncPlayerChatEvent event) {
        format = plugin.getConfig().getString("chat_formats." + format);
        format = format.replace("%player%", player.getName())
                .replace("%message%", event.getMessage());
        Component message = MiniMessage.miniMessage().deserialize(format);

        if (plugin.getConfig().getBoolean("modules.local_chat")) {
            int radius = plugin.getConfig().getInt("local_chat.radius");
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