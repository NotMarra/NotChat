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
import com.notmarra.notchat.listeners.NotChatCommandGroup;
import com.notmarra.notlib.utils.ChatF;
import com.notmarra.notlib.utils.command.NotCommand;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class GamesCommandGroup extends NotChatCommandGroup {
    public static final String ID = "games";

    private final Map<String, Function<NotChat, ChatGame>> gameConstructors = Map.of(
        MathGame.ID, MathGame::new,
        NumberGuessGame.ID, NumberGuessGame::new,
        HangmanGame.ID, HangmanGame::new,
        AnagramGame.ID, AnagramGame::new,
        FastTypeGame.ID, FastTypeGame::new,
        TrueOrFalseGame.ID, TrueOrFalseGame::new
    );

    private final Map<UUID, ChatGame> activeGames = new HashMap<>();

    public GamesCommandGroup(NotChat plugin) {
        super(plugin);
        startGameScheduler();
    }

    @Override
    public String getId() { return ID; }

    @Override
    public List<NotCommand> notCommands() {
        return List.of(
            // TODO: dev only, remove later
            startGameCommand(),
            leaveGameCommand(),

            hintCommand(),
            answerCommand()
        );
    }

    private NotCommand startGameCommand() {
        NotCommand command = NotCommand.of("game", cmd -> {
            ChatF.of("Musíš zadat typ hry").sendTo(cmd.getPlayer());
        });

        command.stringArg("gameType")
            .setSuggestions(getGameTypes())
            .onExecute(arg -> {
                Player player = arg.getPlayer();

                if (hasActiveGame(player)) {                    
                    ChatF.of("Máš již aktivní hru").sendTo(player);
                    return;
                }

                String gameType = arg.get().trim();
                if (!getGameTypes().contains(gameType)) {
                    ChatF.of("Neznámý typ hry: " + gameType).sendTo(player);
                    return;
                }
                
                startGame(player, gameType);
            });

        return command;
    }

    private NotCommand leaveGameCommand() {
        return NotCommand.of("leave", cmd -> {
            Player player = cmd.getPlayer();

            if (!hasActiveGame(player)) {
                ChatF.of("Nemáš aktivní hru").sendTo(player);
                return;
            }

            removeGame(player);
            ChatF.of("Hra ukončena").sendTo(player);
        });
    }

    private NotCommand hintCommand() {
        return NotCommand.of("hint", cmd -> {
            Player player = cmd.getPlayer();

            if (!hasActiveGame(player)) {
                ChatF.of("Nemáš aktivní hru").sendTo(player);
                return;
            }

            ChatGame game = getActiveGame(player);
            game.onHint(player).sendTo(player);
        });
    }

    private NotCommand answerCommand() {
        NotCommand command = NotCommand.of("answer", cmd -> {
            ChatF.of("Musíš zadat odpověď").sendTo(cmd.getPlayer());
        });

        command.stringArg("answer", arg -> {
            Player player = arg.getPlayer();

            if (!hasActiveGame(player)) {
                ChatF.of("Nemáš aktivní hru").sendTo(player);
                return;
            }

            handlePlayerAnswer(player, arg.get());
        });

        return command;
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
        ChatGame game = gameConstructors.get(gameType).apply((NotChat)plugin);
        if (game != null) {
            activeGames.put(player.getUniqueId(), game);
            game.initialize(player);
        }
    }

    public void startRandomGame(Player player) {
        List<String> gameTypes = getGameTypes();
        String randomGameType = gameTypes.get((int) (Math.random() * gameTypes.size()));

        ChatGame game = gameConstructors.get(randomGameType).apply((NotChat)plugin);
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
