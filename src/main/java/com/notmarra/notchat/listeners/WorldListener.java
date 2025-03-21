package com.notmarra.notchat.listeners;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.notmarra.notchat.NotChat;
import com.notmarra.notlib.utils.ChatF;

public class WorldListener extends BaseNotListener {
    public static final String ID = "world";

    private ConfigurationSection groups;

    public WorldListener(NotChat plugin) {
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
        groups = config.getConfigurationSection("groups");
    }

    public void sendMessage(Player player, ChatF message) {
        if (groups == null) {
            player.sendMessage(message.toString());
            return;
        }

        String playerWorld = player.getWorld().getName();
        for (String group : groups.getKeys(false)) {
            if (groups.getStringList(group).contains(playerWorld)) {
                for (Player p : player.getServer().getOnlinePlayers()) {
                    if (groups.getStringList(group).contains(p.getWorld().getName())) {
                        p.sendMessage(message.toString());
                    }
                }
                return;
            }
        }
    }
}
