package com.notmarra.notchat.games;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class GameManager {
    private final Map<ChatGame<?>, Time> activeGames = new HashMap<>();
    private final JavaPlugin plugin;

    public GameManager(JavaPlugin plugin) {
        this.plugin = plugin;
        startGameScheduler();
    }

    public void startGameScheduler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {


        }, 0L, 20L * 60); // Každých 60 sekund (20 tiků = 1 sekunda)
    }

    public void startRandomGame(Player player) {
        String[] gameTypes = {"math", "number_guess", "hangman", "fast_type", "anagram", "true_or_false"};
        String randomGameType = gameTypes[(int) (Math.random() * gameTypes.length)];

        ChatGame<?> game = createGame(randomGameType);
        if (game != null) {
            activeGames.put(game, new Time(System.currentTimeMillis()));
            player.sendMessage("Nová hra: " + randomGameType);
            if (game instanceof MathGame) {
                player.sendMessage(((MathGame) game).generateProblem());
            }
        }
    }

    private ChatGame<?> createGame(String gameType) {
        switch (gameType) {
            case "math":
                return new MathGame("math");
            default:
                return null;
        }
    }

    public void handlePlayerAnswer(Player player, String answer) {
        ChatGame<?> game = activeGames.get(player.getUniqueId());
        if (game != null) {
            ChatGameResponse response = game.onAnswer(player, answer);
            if (response.isCorrect()) {
                player.sendMessage("Správně! Získáváš odměnu.");
                activeGames.remove(player.getUniqueId());
            } else {
                player.sendMessage("Špatně. Zkus to znovu!");
            }
        }
    }
}
