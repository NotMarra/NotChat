package com.notmarra.notchat.cmds;

import com.mojang.brigadier.Command;
import com.notmarra.notchat.utils.ChatFormatter;
import com.notmarra.notchat.utils.Config;
import com.notmarra.notchat.utils.commandHandler.NotCommand;
import com.notmarra.notchat.utils.commandHandler.arguments.NotEntity;
import com.notmarra.notchat.utils.commandHandler.arguments.NotString;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    public static List<NotCommand> Msg() {
        List<String> aliases = Config.getStringList("msg.alias");
        ChatFormatter chat = new ChatFormatter();
        List<NotCommand> commands = new ArrayList<>();

        for (String alias : aliases) {
            NotCommand cmd = new NotCommand(alias);

            cmd.onExecute((ctx) -> {
                ctx.getSource().getSender().sendMessage(chat.format(Config.getString("msg.message_usage")));
                return Command.SINGLE_SUCCESS;
            });

            NotEntity players = new NotEntity("player");
            List<String> suggestions = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            players.setSuggestions(suggestions);
            players.onExecute(ctx -> {
                ctx.getSource().getSender().sendMessage(chat.format(Config.getString("msg.message_usage")));
                return Command.SINGLE_SUCCESS;
            });

            NotString message = sendMSG(players, chat);
            players.addArg(message);
            cmd.addArg(players);

            commands.add(cmd);
        }

        return commands;
    }

    private static @NotNull NotString sendMSG(NotEntity players, ChatFormatter chat) {
        NotString message = new NotString("message");
        message.onExecute(ctx -> {
            List<Entity> targetPlayers = players.get(ctx);
            String msg = message.get(ctx);

            CommandSender sender = ctx.getSource().getSender();
            String senderName = sender.getName();

            for (Entity p : targetPlayers) {
                if (p instanceof Player targetPlayer) {
                    String targetName = targetPlayer.getName();

                    // Send message to sender
                    sender.sendMessage(chat.format(Config.getString("msg.message_sender")
                            .replace("%player%", senderName)
                            .replace("%target%", targetName)
                            .replace("%message%", msg)));

                    // Send message to receiver
                    targetPlayer.sendMessage(chat.format(Config.getString("msg.message_receiver")
                            .replace("%player%", senderName)
                            .replace("%target%", targetName)
                            .replace("%message%", msg)));
                }
            }

            return Command.SINGLE_SUCCESS;
        });
        return message;
    }
}
