package org.example.notchat.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.example.notchat.utils.ChatFormatter;
import org.example.notchat.utils.ChatGroup;
import org.example.notchat.utils.ChatManager;

public class ChatListener implements Listener {
    private final ChatManager chatManager;
    private final ChatFormatter chatFormatter;

    public ChatListener(ChatManager chatManager, ChatFormatter chatFormatter) {
        this.chatManager = chatManager;
        this.chatFormatter = chatFormatter;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String message = event.message().toString();
        ChatGroup group = chatManager.getPlayerGroup(player);
        Component finalMessage = chatFormatter.formatMessage(player, group.getFormat(), message);
        event.message(finalMessage);
    }
}