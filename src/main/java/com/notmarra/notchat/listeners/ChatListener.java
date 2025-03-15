package com.notmarra.notchat.listeners;

import com.notmarra.notchat.NotChat;
import com.notmarra.notchat.utils.ChatFormatter;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Set;



public class ChatListener implements Listener {
    private final NotChat plugin;

    public ChatListener(NotChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
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

    private void sendMessage(Player player, String format, AsyncChatEvent event) {
        format = plugin.getConfig().getString("chat_formats." + format);


        ChatFormatter message = ChatFormatter.of(format)
            .replace(ChatFormatter.K_PLAYER, player.getName())
            .replace(ChatFormatter.K_MESSAGE, event.message());
        
        if (plugin.getConfig().getBoolean("modules.local_chat")) {
            int radius = plugin.getConfig().getInt("local_chat.radius");
            for (Player p : player.getWorld().getPlayers()) {
                if (p.getLocation().distance(player.getLocation()) <= radius) {
                    p.sendMessage(message.build());
                }
            }
        } else {
            player.getServer().broadcast(message.build());
        }
    
        event.setCancelled(true);
    }
}