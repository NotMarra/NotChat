package com.notmarra.notchat.listeners.chat.modules;

import com.notmarra.notchat.NotChat;
import com.notmarra.notchat.listeners.NotChatListener;
import com.notmarra.notlib.utils.ChatF;

import net.kyori.adventure.text.Component;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class LocalChatListener extends NotChatListener {
    public static final String ID = "local";

    private double radius;

    public LocalChatListener(NotChat plugin) {
        super(plugin);
        registerConfigurable();
    }

    @Override
    public String getId() { return ID; }

    @Override
    public void onConfigReload(List<String> reloadedConfigs) {
        FileConfiguration config = getConfig(getModuleConfigPath());

        radius = config.getDouble("radius");
    }

    public boolean canSendMessage(Player src, Player dest) {
        if (!isEnabled()) return true;

        if (src.getWorld() != dest.getWorld()) {
            return false;
        }

        if (src.getLocation().distance(dest.getLocation()) > radius) {
            return false;
        }

        return true;
    }

    public void sendMessage(Player player, Component message) {
        if (isEnabled()) {
            localBroadcast(player, ChatF.of(message));
        } else {
            getServer().broadcast(message);
        }
    }

    public void localBroadcast(Player player, ChatF message) {
        for (Player p : player.getWorld().getPlayers()) {
            if (p.getLocation().distance(player.getLocation()) <= radius) {
                message.sendTo(p);
            }
        }
    }
}