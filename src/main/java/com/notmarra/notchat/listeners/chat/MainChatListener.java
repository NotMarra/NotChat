package com.notmarra.notchat.listeners.chat;

import com.notmarra.notchat.NotChat;
import com.notmarra.notchat.listeners.NotChatListener;
import com.notmarra.notchat.listeners.chat.modules.FilterChatListener.FilterResult;
import com.notmarra.notlib.utils.ChatF;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class MainChatListener extends NotChatListener {
    public static final String ID = "main";

    public MainChatListener(NotChat plugin) { super(plugin); }

    @Override
    public String getId() { return ID; }

    @Override
    public boolean isModule() { return false; }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        String origMessage = ChatF.empty().append(event.message()).buildString();

        // Original -> Filter -> Color -> Format -> Send (World/Local)

        FilterResult result = plugin.getFilterChatListener().processMessage(player, origMessage);
        if (result.isBlocked()) {
            result.getBlockMessage().sendTo(player);
        } else {
            String filtered = result.getFilteredMessage();
            Component msg = plugin.getColorChatInvListener().applyColorToMessage(player, filtered);
            msg = plugin.getFormatChatListener().formatMessage(player, msg);
            plugin.getWorldListener().sendMessage(player, msg);
        }
    }
}