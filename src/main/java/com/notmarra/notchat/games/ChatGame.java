package com.notmarra.notchat.games;

import com.notmarra.notlib.utils.ChatF;
import com.notmarra.notchat.utils.ConfigFiles;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;


public abstract class ChatGame {
    public JavaPlugin plugin;
    public String id;
    public long startTime;
    public ConfigurationSection settings;
    public List<String> rewardCommands;

    public ChatGame(JavaPlugin plugin, String id) {
        this.plugin = plugin;
        this.id = id;
        this.startTime = System.currentTimeMillis();
        this.settings = ConfigFiles.getConfigurationSection("games.yml", id + ".settings");
        if (this.settings == null) {
            this.settings = ConfigFiles.emptyConfigurationSection();
        }
        this.rewardCommands = ConfigFiles.getStringList("games.yml", id + ".reward_commands");
    }

    public abstract ChatF getGameSummary();

    public ChatF getTitle() {
        return ChatF.of("[[[ " + id + " ]]]", ChatF.C_DODGERBLUE);
    }

    public void initialize(Player player) {
        player.sendMessage(getTitle().withPlayer(player).build());
        start(player);
    }

    public abstract void start(Player player);

    public abstract ChatF onHint(Player player);

    public abstract ChatGameResponse onAnswer(Player player, String answer);

    // NOTE: you should not override, but can i stop you from doing it?
    public void endCorrect(Player player) {
        player.sendMessage(getGameSummary().withPlayer(player).build());
        player.sendMessage(Component.text("===> ZÍSKÁVÁŠ ODMĚNU <===", TextColor.color(0, 255, 0)));
        for (String command : rewardCommands) {
            player.performCommand(ChatF.of(command).withPlayer(player).buildString());
        }
    }

    // NOTE: you can override
    public void endIncorrect(Player player) {
        player.sendMessage(getGameSummary().withPlayer(player).build());
        player.sendMessage(Component.text("===> SMŮLA <===", TextColor.color(255, 0, 0)));
    }
}
