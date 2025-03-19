package com.notmarra.notchat.games;

import com.notmarra.notlib.utils.ChatF;
import com.notmarra.notchat.NotChat;
import com.notmarra.notchat.utils.ConfigFiles;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public abstract class ChatGame {
    public NotChat plugin;
    public long startTime;
    public ConfigurationSection settings;
    public List<String> rewardCommands;

    public ChatGame(NotChat plugin) {
        this.plugin = plugin;
        this.startTime = System.currentTimeMillis();

        this.settings = ConfigFiles.getConfigurationSection("games.yml", getId() + ".settings");
        if (this.settings == null) {
            this.settings = ConfigFiles.emptyConfigurationSection();
        }
        this.rewardCommands = ConfigFiles.getStringList("games.yml", getId() + ".reward_commands");
    }

    public abstract String getId();

    public abstract ChatF getGameSummary();

    public ChatF getTitle() {
        return ChatF.of("[[[ " + getId() + " ]]]", ChatF.C_DODGERBLUE);
    }

    public void initialize(Player player) {
        getTitle().sendTo(player);
        start(player);
    }

    public abstract void start(Player player);

    public abstract ChatF onHint(Player player);

    public abstract ChatGameResponse onAnswer(Player player, String answer);

    public void end(Player player, ChatGameResponse response) {
        if (response.correct) {
            endCorrect(player, response);
        } else {
            endIncorrect(player, response);
        }
    }

    // NOTE: you should not override, but can i stop you from doing it?
    public void endCorrect(Player player, ChatGameResponse response) {
        getGameSummary().sendTo(player);

        ChatF.of("===> ZÍSKÁVÁŠ ODMĚNU <===", ChatF.C_GREEN).sendTo(player);
        
        List<String> rewardCommands = new ArrayList<>(this.rewardCommands);
        rewardCommands.addAll(response.rewardCommands);

        for (String command : rewardCommands) {
            player.performCommand(ChatF.of(command).withPlayer(player).buildString());
        }
    }

    // NOTE: you can override
    public void endIncorrect(Player player, ChatGameResponse response) {
        getGameSummary().sendTo(player);
        ChatF.of("===> SMŮLA <===", ChatF.C_RED).sendTo(player);
    }
}
