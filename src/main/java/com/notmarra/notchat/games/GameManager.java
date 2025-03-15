package com.notmarra.notchat.games;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.brigadier.Command;
import com.notmarra.notchat.games.anagram.AnagramGame;
import com.notmarra.notchat.games.fast_type.FastTypeGame;
import com.notmarra.notchat.games.hangman.HangmanGame;
import com.notmarra.notchat.games.math.MathGame;
import com.notmarra.notchat.games.number_guess.NumberGuessGame;
import com.notmarra.notchat.utils.commandHandler.NotCommand;
import com.notmarra.notchat.utils.commandHandler.arguments.NotGreedyStringArg;
import com.notmarra.notchat.utils.commandHandler.arguments.NotStringArg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

public class GameManager {
    private final JavaPlugin plugin;

    private final Map<String, BiFunction<JavaPlugin, String, ChatGame>> gameConstructors = new HashMap<>();
    private final Map<UUID, ChatGame> activeGames = new HashMap<>();

    public GameManager(JavaPlugin plugin) {
        this.plugin = plugin;
        registerGameConstructors();
        startGameScheduler();
    }

    public List<String> getGameTypes() {
        return List.copyOf(gameConstructors.keySet());
    }

    public void registerGameConstructors() {
        gameConstructors.put(MathGame.GAME_ID, MathGame::new);
        gameConstructors.put(NumberGuessGame.GAME_ID, NumberGuessGame::new);
        gameConstructors.put(HangmanGame.GAME_ID, HangmanGame::new);
        gameConstructors.put(AnagramGame.GAME_ID, AnagramGame::new);
        gameConstructors.put(FastTypeGame.GAME_ID, FastTypeGame::new);
    }

    public void startGameScheduler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            
        }, 0L, 20L * 60); // Každých 60 sekund (20 tiků = 1 sekunda)
    }

    public boolean hasActiveGame(Player player) {
        return activeGames.containsKey(player.getUniqueId());
    }

    public void startGame(Player player, String gameType) {
        ChatGame game = gameConstructors.get(gameType).apply(plugin, gameType);
        if (game != null) {
            activeGames.put(player.getUniqueId(), game);
            game.start(player);
        }
    }

    public void startRandomGame(Player player) {
        List<String> gameTypes = getGameTypes();
        String randomGameType = gameTypes.get((int) (Math.random() * gameTypes.size()));

        ChatGame game = gameConstructors.get(randomGameType).apply(plugin, randomGameType);
        if (game != null) {
            activeGames.put(player.getUniqueId(), game);
            game.start(player);
        }
    }

    public void handlePlayerAnswer(Player player, String answer) {
        ChatGame game = activeGames.get(player.getUniqueId());
        if (game != null) {
            ChatGameResponse response = game.onAnswer(player, answer);

            if (response.endingGame) {
                activeGames.remove(player.getUniqueId());

                if (response.correct) {
                    game.endCorrect(player);
                } else {
                    game.endIncorrect(player);
                }
            }
        }
    }

    public List<NotCommand> getCommands() {
        return List.of(
            // TODO: dev only, remove later
            startGameCommand(),
            leaveGameCommand(),

            hintCommand(),
            answerCommand()
        );
    }

    public NotCommand startGameCommand() {
        NotCommand gameCmd = new NotCommand("game");

        NotStringArg gameTypeArg = new NotStringArg("gameType");
        gameTypeArg.setSuggestions(getGameTypes());
        gameTypeArg.onExecute(ctx -> {
            Entity entity = ctx.getSource().getExecutor();
            if (!(entity instanceof Player player)) {
                return Command.SINGLE_SUCCESS;
            }

            if (hasActiveGame(player)) {                    
                player.sendMessage("Máš již aktivní hru");
                return Command.SINGLE_SUCCESS;
            }

            String gameType = gameTypeArg.get(ctx).trim();
            if (!getGameTypes().contains(gameType)) {
                player.sendMessage("Neznámý typ hry: " + gameType);
                return Command.SINGLE_SUCCESS;
            }

            startGame(player, gameType);
            return Command.SINGLE_SUCCESS;
        });

        gameCmd.addArg(gameTypeArg);

        return gameCmd;
    }

    public NotCommand leaveGameCommand() {
        NotCommand leaveCmd = new NotCommand("leave");

        leaveCmd.onExecute(ctx -> {
            Entity entity = ctx.getSource().getExecutor();
            if (!(entity instanceof Player player)) {
                return Command.SINGLE_SUCCESS;
            }

            if (!hasActiveGame(player)) {
                player.sendMessage("Nemáš aktivní hru");
                return Command.SINGLE_SUCCESS;
            }

            activeGames.remove(player.getUniqueId());
            player.sendMessage("Hra ukončena");
            return Command.SINGLE_SUCCESS;
        });

        return leaveCmd;
    }

    public NotCommand hintCommand() {
        NotCommand answerCmd = new NotCommand("hint");

        answerCmd.onExecute(ctx -> {
            Entity entity = ctx.getSource().getExecutor();
            if (!(entity instanceof Player player)) {
                return Command.SINGLE_SUCCESS;
            }

            if (!hasActiveGame(player)) {
                player.sendMessage("Nemáš aktivní hru");
                return Command.SINGLE_SUCCESS;
            }

            activeGames.get(player.getUniqueId()).onHint(player);

            return Command.SINGLE_SUCCESS;
        });

        return answerCmd;
    }

    public NotCommand answerCommand() {
        NotCommand answerCmd = new NotCommand("answer");
        answerCmd.onExecute(ctx -> {
            ctx.getSource().getSender().sendMessage("Musíš zadat odpověď");
            return Command.SINGLE_SUCCESS;
        });

        NotGreedyStringArg answerArg = new NotGreedyStringArg("answer");
        answerArg.onExecute(ctx -> {
            Entity entity = ctx.getSource().getExecutor();
            if (!(entity instanceof Player player)) {
                return Command.SINGLE_SUCCESS;
            }

            if (!hasActiveGame(player)) {
                player.sendMessage("Nemáš aktivní hru");
                return Command.SINGLE_SUCCESS;
            }

            String answer = answerArg.get(ctx);

            if (answer.trim().isEmpty()) {
                player.sendMessage("Neplatná odpověď");
                return Command.SINGLE_SUCCESS;
            }

            handlePlayerAnswer(player, answerArg.get(ctx));
            return Command.SINGLE_SUCCESS;
        });

        answerCmd.addArg(answerArg);

        return answerCmd;
    }
}
