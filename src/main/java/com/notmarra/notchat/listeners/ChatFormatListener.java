package com.notmarra.notchat.listeners;

import com.notmarra.notchat.NotChat;
import com.notmarra.notlib.utils.ChatF;

import io.papermc.paper.event.player.AsyncChatEvent;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ChatFormatListener extends BaseNotListener {
    public static final String ID = "format";

    private ConfigurationSection formats;

    public ChatFormatListener(NotChat plugin) {
        super(plugin);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean hasConfig() {
        return true;
    }

    @Override
    public void loadConfig() {
        formats = config.getConfigurationSection("formats");
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

            if (formats.contains(group)) {
                sendMessage(player, group, event);
                return;
            }
        } else {
            for (String type : formats.getKeys(false)) {
                if (player.hasPermission(type)) {
                    sendMessage(player, type, event);
                    return;
                }
            }
        }

        sendMessage(player, "default", event);
    }

    private void sendMessage(Player player, String type, AsyncChatEvent event) {
        LocalChatListener localChatListener = (LocalChatListener) getListener(LocalChatListener.ID);

        String format = formats.getString(type);

        // TODO: if format is null (invalid config file) use some hardcoded fallback

        ChatF message = ChatF.empty()
            .append(format)
            .withPlayer(player)
            .replace(ChatF.K_MESSAGE, event.message());
        
        if (localChatListener.isEnabled()) {
            localChatListener.sendMessage(player, message);
        } else {
            player.getServer().broadcast(message.build());
        }
    
        event.setCancelled(true);
    }
}