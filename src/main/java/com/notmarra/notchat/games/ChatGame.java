package com.notmarra.notchat.games;

import com.notmarra.notchat.utils.ConfigFiles;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;


public abstract class ChatGame<T extends ChatGameResponse> {
    private String id;

    public ChatGame(String id) {
        this.id = id;
    }

    public abstract T onAnswer(Player player, String answer);

    public ConfigurationSection getSettings() {
        return ConfigFiles.getConfigurationSection("games.yml",id + ".settings");
    }

    public List<String> getRewardCommands() {
        return ConfigFiles.getStringList("games.yml",id + ".reward_commands");
    }
}
