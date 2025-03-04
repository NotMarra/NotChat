package com.notmarra.notchat.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class ChatFormatter {
    private final MiniMessage miniMessage;

    public ChatFormatter() {
        this.miniMessage = MiniMessage.miniMessage();
    }

    public Component formatMessage(Player player, String format, String message) {
        String processedFormat = PlaceholderAPI.setPlaceholders(player, format);
        processedFormat = processedFormat.replace("%message%", message);
        return miniMessage.deserialize(processedFormat);
    }
}