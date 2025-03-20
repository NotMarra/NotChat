package com.notmarra.notchat.listeners;

import com.notmarra.notchat.NotChat;
import com.notmarra.notchat.listeners.FilterListener.FilterResult;
import com.notmarra.notlib.utils.ChatF;

import io.papermc.paper.event.player.AsyncChatEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class MainChatListener extends BaseNotListener {
    public static final String ID = "main";

    public MainChatListener(NotChat plugin) {
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

    @Override
    public boolean isModule() {
        return false;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String message = ChatF.empty().append(event.message()).buildString();

        FilterListener filter = (FilterListener) plugin.getListener(FilterListener.ID);
        if (filter != null && filter.isEnabled()) {
            FilterResult result = filter.processMessage(player, message);

            if (result.isBlocked()) {
                event.setCancelled(true);
                ChatF.of(result.getBlockMessage(), ChatF.C_RED).sendTo(player);
                return;
            } else {
                message = result.getFilteredMessage();
            }
        }

        FormatListener format = (FormatListener) plugin.getListener(FormatListener.ID);
        if (format != null && format.isEnabled()) {
            message = format.formatMessage(player, message);
        }

        LocalListener local = (LocalListener) plugin.getListener(LocalListener.ID);
        if (local != null && local.isEnabled()) {
            local.sendMessage(player, ChatF.of(message));
        } else {
            player.getServer().broadcast(ChatF.of(message).build());
        }

        event.setCancelled(true);
    }
}