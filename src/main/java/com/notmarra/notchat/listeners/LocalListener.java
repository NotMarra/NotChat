package com.notmarra.notchat.listeners;

import com.notmarra.notchat.NotChat;
import com.notmarra.notlib.utils.ChatF;

import org.bukkit.entity.Player;

public class LocalListener extends BaseNotListener {
    public static final String ID = "local";

    private double radius;

    public LocalListener(NotChat plugin) {
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
        radius = config.getDouble("radius");
    }

    public void sendMessage(Player player, ChatF message) {
        for (Player p : player.getWorld().getPlayers()) {
            if (p.getLocation().distance(player.getLocation()) <= radius) {
                message.sendTo(p);
            }
        }
    }
}