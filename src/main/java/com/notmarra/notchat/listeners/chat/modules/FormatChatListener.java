package com.notmarra.notchat.listeners.chat.modules;

import com.notmarra.notchat.NotChat;
import com.notmarra.notchat.listeners.NotChatListener;
import com.notmarra.notlib.NotLib;
import com.notmarra.notlib.utils.ChatF;

import io.papermc.paper.event.player.AsyncChatEvent;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
public class FormatChatListener extends NotChatListener {
    public static final String ID = "format";

    private ConfigurationSection formats;

    public FormatChatListener(NotChat plugin) { super(plugin); }

    @Override
    public String getId() { return ID; }

    @Override
    public void loadConfig() {
        formats = config.getConfigurationSection("formats");
    }

    public String getFormat(Player player) {
        if (NotLib.hasVault()) {
            String group = NotChat.getPerms().getPrimaryGroup(player);

            if (group == null) {
                return "default";
            }

            if (formats.contains(group)) {
                return group;
            }
        } else {
            for (String type : formats.getKeys(false)) {
                if (player.hasPermission(type)) {
                    return type;
                }
            }
        }

        return "default";
    }

    public ChatF formatMessage(Player player, AsyncChatEvent event) {
        return formatMessage(player, ChatF.of(event.message()).build());
    }

    public ChatF formatMessage(Player player, Object message) {
        String formatType = getFormat(player);

        return ChatF.empty()
            .append(formats.getString(formatType))
            .withPlayer(player)
            .replace(ChatF.K_MESSAGE, message);
    }
}