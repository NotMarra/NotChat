package com.notmarra.notchat.listeners.chat;

import com.notmarra.notchat.NotChat;
import com.notmarra.notchat.listeners.BaseNotListener;
import com.notmarra.notchat.listeners.chat.modules.FilterChatListener;
import com.notmarra.notchat.listeners.chat.modules.FormatChatListener;
import com.notmarra.notchat.listeners.chat.modules.LocalChatListener;
import com.notmarra.notchat.listeners.chat.modules.FilterChatListener.FilterResult;
import com.notmarra.notchat.listeners.inventory.ColorChatInvListener;
import com.notmarra.notlib.utils.ChatF;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;

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

        FilterChatListener filter = plugin.getFilterChatListener();
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

        Component finalMessage = ChatF.of(message).build();

        getLogger().info("Message: " + message);
        
        ColorChatInvListener color = plugin.getColorChatInvListener();
        if (color != null && color.isEnabled()) {
            finalMessage = color.applyColorToMessage(player, message);
        }

        getLogger().info("Colored message: " + ChatF.of(finalMessage).buildString());

        FormatChatListener format = plugin.getFormatChatListener();
        if (format != null && format.isEnabled()) {
            finalMessage = format.formatMessage(player, finalMessage).build();
        }

        getLogger().info("Formatted message: " + ChatF.of(finalMessage).buildString());

        LocalChatListener local = plugin.getLocalChatListener();
        if (local != null && local.isEnabled()) {
            local.sendMessage(player, finalMessage);
        } else {
            getServer().broadcast(finalMessage);
        }

        // TODO: this, don't forget for the local chat listener
        // WorldListener world = (WorldListener) plugin.getListener(WorldListener.ID);
        // if (world != null && world.isEnabled()) {
        //     world.sendMessage(player, ChatF.of(message));
        // }

        event.setCancelled(true);
    }

    // @EventHandler
    // public void onChat(AsyncChatEvent event) {
    //     Player player = event.getPlayer();
    //     String message = ChatF.empty().append(event.message()).buildString();

    //     FilterChatListener filter = plugin.getFilterChatListener();
    //     if (filter != null && filter.isEnabled()) {
    //         FilterResult result = filter.processMessage(player, message);

    //         if (result.isBlocked()) {
    //             event.setCancelled(true);
    //             ChatF.of(result.getBlockMessage(), ChatF.C_RED).sendTo(player);
    //             return;
    //         } else {
    //             message = result.getFilteredMessage();
    //         }
    //     }

    //     getLogger().info("Message: " + message);

    //     ChatF finalMessage = ChatF.of(message);
        
    //     ColorChatInvListener color = plugin.getColorChatInvListener();
    //     if (color != null && color.isEnabled()) {
    //         finalMessage = color.applyColorToMessage(player, message);
    //     }

    //     getLogger().info("Colored message: " + finalMessage.buildString());

    //     FormatChatListener format = plugin.getFormatChatListener();
    //     if (format != null && format.isEnabled()) {
    //         finalMessage = ChatF.of(format.formatMessage(player, finalMessage));
    //     }

    //     getLogger().info("Formatted message: " + finalMessage.buildString());

    //     LocalChatListener local = plugin.getLocalChatListener();
    //     if (local != null && local.isEnabled()) {
    //         local.sendMessage(player, finalMessage);
    //     } else {
    //         getServer().broadcast(finalMessage.build());
    //     }

    //     // TODO: this, don't forget for the local chat listener
    //     // WorldListener world = (WorldListener) plugin.getListener(WorldListener.ID);
    //     // if (world != null && world.isEnabled()) {
    //     //     world.sendMessage(player, ChatF.of(message));
    //     // }

    //     event.setCancelled(true);
    // }
}