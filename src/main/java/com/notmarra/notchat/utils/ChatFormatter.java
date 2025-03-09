package com.notmarra.notchat.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ChatFormatter {
    private final MiniMessage miniMessage;

    public ChatFormatter() {
        this.miniMessage = MiniMessage.miniMessage();
    }

    public Component format(String message) {
        return miniMessage.deserialize(message);
    }

    public Component format(String message, String player) {
        return miniMessage.deserialize(message.replace("%player%", player));
    }
}