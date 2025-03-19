package com.notmarra.notchat.cmds;

import com.notmarra.notchat.NotChat;
import com.notmarra.notchat.games.ChatGame;
import com.notmarra.notchat.games.ChatGameResponse;
import com.notmarra.notchat.games.anagram.AnagramGame;
import com.notmarra.notchat.games.fast_type.FastTypeGame;
import com.notmarra.notchat.games.hangman.HangmanGame;
import com.notmarra.notchat.games.math.MathGame;
import com.notmarra.notchat.games.number_guess.NumberGuessGame;
import com.notmarra.notchat.games.true_or_false.TrueOrFalseGame;
import com.notmarra.notlib.utils.ChatF;
import com.notmarra.notlib.utils.command.NotCommand;
import com.notmarra.notlib.utils.command.arguments.NotGreedyStringArg;
import com.notmarra.notlib.utils.command.arguments.NotStringArg;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class GamesCommandManager extends BaseNotCommandManager {
    public static final String ID = "games";

    private final Map<String, Function<NotChat, ChatGame>> gameConstructors = new HashMap<>();
    private final Map<UUID, ChatGame> activeGames = new HashMap<>();

    public GamesCommandManager(NotChat plugin) {
        super(plugin);

        gameConstructors.put(MathGame.ID, MathGame::new);
        gameConstructors.put(NumberGuessGame.ID, NumberGuessGame::new);
        gameConstructors.put(HangmanGame.ID, HangmanGame::new);
        gameConstructors.put(AnagramGame.ID, AnagramGame::new);
        gameConstructors.put(FastTypeGame.ID, FastTypeGame::new);
        gameConstructors.put(TrueOrFalseGame.ID, TrueOrFalseGame::new);
        startGameScheduler();
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

    }

    @Override
    public List<NotCommand> buildCommands() {
        return List.of(
            // TODO: dev only, remove later
            startGameCommand(),
            leaveGameCommand(),

            hintCommand(),
            answerCommand()
        );
    }

    private NotCommand startGameCommand() {
        NotCommand gameCmd = new NotCommand("game");

        NotStringArg gameTypeArg = new NotStringArg("gameType");
        gameTypeArg.setSuggestions(getGameTypes());
        gameTypeArg.onExecute(ctx -> {
            Entity entity = ctx.getSource().getExecutor();
            if (!(entity instanceof Player player)) return;

            if (hasActiveGame(player)) {                    
                ChatF.of("Máš již aktivní hru").sendTo(player);
                return;
            }

            String gameType = gameTypeArg.get(ctx).trim();
            if (!getGameTypes().contains(gameType)) {
                ChatF.of("Neznámý typ hry: " + gameType).sendTo(player);
                return;
            }
            
            startGame(player, gameType);
        });

        gameCmd.addArg(gameTypeArg);

        return gameCmd;
    }

    private NotCommand leaveGameCommand() {
        NotCommand leaveCmd = new NotCommand("leave");

        leaveCmd.onExecute(ctx -> {
            Entity entity = ctx.getSource().getExecutor();
            if (!(entity instanceof Player player)) return;

            if (!hasActiveGame(player)) {
                ChatF.of("Nemáš aktivní hru").sendTo(player);
                return;
            }

            removeGame(player);
            ChatF.of("Hra ukončena").sendTo(player);
        });

        return leaveCmd;
    }

    private NotCommand hintCommand() {
        NotCommand answerCmd = new NotCommand("hint");

        answerCmd.onExecute(ctx -> {
            Entity entity = ctx.getSource().getExecutor();
            if (!(entity instanceof Player player)) return;

            if (!hasActiveGame(player)) {
                ChatF.of("Nemáš aktivní hru").sendTo(player);
                return;
            }
            
            ChatGame game = getActiveGame(player);
            game.onHint(player).sendTo(player);
        });

        return answerCmd;
    }

    private NotCommand answerCommand() {
        NotCommand answerCmd = new NotCommand("answer");
        answerCmd.onExecute(ctx -> {
            Entity entity = ctx.getSource().getExecutor();
            ChatF.of("Musíš zadat odpověď").sendTo(entity);
        });

        NotGreedyStringArg answerArg = new NotGreedyStringArg("answer");
        answerArg.onExecute(ctx -> {
            Entity entity = ctx.getSource().getExecutor();
            if (!(entity instanceof Player player)) return;

            if (!hasActiveGame(player)) {
                ChatF.of("Nemáš aktivní hru").sendTo(player);
                return;
            }

            String answer = answerArg.get(ctx);

            if (answer.trim().isEmpty()) {
                ChatF.of("Neplatná odpověď").sendTo(player);
                return;
            }

            handlePlayerAnswer(player, answer);
        });

        answerCmd.addArg(answerArg);

        return answerCmd;
    }

    public List<String> getGameTypes() {
        return List.copyOf(gameConstructors.keySet());
    }

    public void startGameScheduler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            
        }, 0L, 20L * 60); // Každých 60 sekund (20 tiků = 1 sekunda)
    }

    public boolean hasActiveGame(Player player) {
        return activeGames.containsKey(player.getUniqueId());
    }

    public void startGame(Player player, String gameType) {
        ChatGame game = gameConstructors.get(gameType).apply(plugin);
        if (game != null) {
            activeGames.put(player.getUniqueId(), game);
            game.initialize(player);
        }
    }

    public void startRandomGame(Player player) {
        List<String> gameTypes = getGameTypes();
        String randomGameType = gameTypes.get((int) (Math.random() * gameTypes.size()));

        ChatGame game = gameConstructors.get(randomGameType).apply(plugin);
        if (game != null) {
            activeGames.put(player.getUniqueId(), game);
            game.initialize(player);
        }
    }

    public void handlePlayerAnswer(Player player, String answer) {
        ChatGame game = activeGames.get(player.getUniqueId());
        if (game != null) {
            ChatGameResponse response = game.onAnswer(player, answer);

            if (response.endingGame) {
                activeGames.remove(player.getUniqueId());
                game.end(player, response);
            }
        }
    }

    public ChatGame getActiveGame(Player player) {
        return activeGames.get(player.getUniqueId());
    }

    public void removeGame(Player player) {
        activeGames.remove(player.getUniqueId());
    }
}
