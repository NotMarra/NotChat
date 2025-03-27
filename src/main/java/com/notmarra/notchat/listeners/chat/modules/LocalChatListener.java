package com.notmarra.notchat.listeners.chat.modules;

import com.notmarra.notchat.NotChat;
import com.notmarra.notchat.listeners.NotChatListener;
import com.notmarra.notlib.utils.ChatF;

import net.kyori.adventure.text.Component;

import org.bukkit.entity.Player;

public class LocalChatListener extends NotChatListener {
    public static final String ID = "local";

    private double radius;

    public LocalChatListener(NotChat plugin) { super(plugin); }

    @Override
    public String getId() { return ID; }

    @Override
    public void loadConfig() {
        radius = config.getDouble("radius");
    }

    public void sendMessage(Player player, Component message) {
        sendMessage(player, ChatF.of(message));
    }

    public void sendMessage(Player player, ChatF message) {
        for (Player p : player.getWorld().getPlayers()) {
            if (p.getLocation().distance(player.getLocation()) <= radius) {
                message.sendTo(p);
            }
        }
    }
}