package com.notmarra.notchat.listeners.chat.modules;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.notmarra.notchat.NotChat;
import com.notmarra.notchat.listeners.NotChatListener;
import com.notmarra.notlib.utils.ChatF;

import net.kyori.adventure.text.Component;

public class WorldListener extends NotChatListener {
    public static final String ID = "world";

    private ConfigurationSection groups;

    public WorldListener(NotChat plugin) {
        super(plugin);
        registerConfigurable();
    }

    @Override
    public String getId() { return ID; }

    @Override
    public void onConfigReload(List<String> reloadedConfigs) {
        FileConfiguration config = getConfig(getModuleConfigPath());

        groups = config.getConfigurationSection("groups");
    }

    public void sendMessage(Player player, Component message) {
        if (isEnabled()) {
            broadcast(player, ChatF.of(message));
        } else {
            plugin.getLocalChatListener().sendMessage(player, message);
        }
    }

    public void broadcast(Player player, ChatF message) {
        if (groups == null) {
            player.sendMessage(message.toString());
            return;
        }

        LocalChatListener local = plugin.getLocalChatListener();
        String playerWorld = player.getWorld().getName();
        for (String group : groups.getKeys(false)) {
            if (groups.getStringList(group).contains(playerWorld)) {
                for (Player p : getServer().getOnlinePlayers()) {
                    boolean canSend = local.canSendMessage(player, p);
                    boolean isInGroup = groups.getStringList(group).contains(p.getWorld().getName());
                    if (canSend && isInGroup) p.sendMessage(message.toString());
                }
                return;
            }
        }
    }
}
