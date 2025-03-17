package com.notmarra.notchat.listeners;

import com.notmarra.notchat.NotChat;
import com.notmarra.notlib.utils.ChatF;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {
    private final NotChat plugin;

    public ChatListener(NotChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        if (NotChat.hasVault()) {
            String group = NotChat.getPerms().getPrimaryGroup(player);

            if (group == null) {
                sendMessage(player, "default", event);
                return;
            }

            if (NotChat.getChatFormats().contains(group)) {
                sendMessage(player, group, event);
                return;
            }
        } else {
            for (String type : NotChat.getChatFormats().getKeys(false)) {
                if (player.hasPermission(type)) {
                    sendMessage(player, type, event);
                    return;
                }
            }
        }

        sendMessage(player, "default", event);
    }

    private void sendMessage(Player player, String type, AsyncChatEvent event) {
        String format = NotChat.getChatFormats().getString(type);

        // TODO: if format is null (invalid config file) use some hardcoded fallback

        ChatF message = ChatF.empty()
            .append(format)
            .withPlayer(player)
            .replace(ChatF.K_MESSAGE, event.message());
        
        if (plugin.getConfig().getBoolean("modules.local_chat")) {
            int radius = plugin.getConfig().getInt("local_chat.radius");
            for (Player p : player.getWorld().getPlayers()) {
                if (p.getLocation().distance(player.getLocation()) <= radius) {
                    message.sendTo(p);
                }
            }
        } else {
            player.getServer().broadcast(message.build());
        }
    
        event.setCancelled(true);
    }
}