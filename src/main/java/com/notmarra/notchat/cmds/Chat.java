package com.notmarra.notchat.cmds;

import com.mojang.brigadier.Command;
import com.notmarra.notchat.utils.ChatFormatter;
import com.notmarra.notchat.utils.Config;
import com.notmarra.notchat.utils.commandHandler.NotCommand;
import com.notmarra.notchat.utils.commandHandler.arguments.NotPlayerArg;
import com.notmarra.notchat.utils.commandHandler.arguments.NotStringArg;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    public static List<NotCommand> Msg() {
        List<String> aliases = Config.getStringList("msg.alias");
        List<NotCommand> commands = new ArrayList<>();

        for (String alias : aliases) {
            NotCommand cmd = new NotCommand(alias);

            cmd.onExecute((ctx) -> {
                ChatFormatter message = ChatFormatter.of(Config.getString("msg.message_usage"));
                ctx.getSource().getSender().sendMessage(message.build());
                return Command.SINGLE_SUCCESS;
            });

            NotPlayerArg players = new NotPlayerArg("player");
            List<String> suggestions = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            players.setSuggestions(suggestions);
            players.onExecute(ctx -> {
                ChatFormatter message = ChatFormatter.of(Config.getString("msg.message_usage"));
                ctx.getSource().getSender().sendMessage(message.build());
                return Command.SINGLE_SUCCESS;
            });

            NotStringArg message = sendMSG(players);
            players.addArg(message);
            cmd.addArg(players);

            commands.add(cmd);
        }

        return commands;
    }

    private static NotStringArg sendMSG(NotPlayerArg players) {
        NotStringArg message = new NotStringArg("message");
        message.onExecute(ctx -> {
            List<Player> targetPlayers = players.get(ctx);
            String msg = message.get(ctx);

            CommandSender sender = ctx.getSource().getSender();
            String senderName = sender.getName();

            for (Player player : targetPlayers) {
                String targetName = player.getName();

                // Send message to sender
                ChatFormatter senderMessage = ChatFormatter.of(Config.getString("msg.message_sender"))
                    .replace(ChatFormatter.K_PLAYER, senderName)
                    .replace(ChatFormatter.K_TARGET, targetName)
                    .replace(ChatFormatter.K_MESSAGE, msg);
                sender.sendMessage(senderMessage.build());

                // Send message to receiver
                ChatFormatter formatter = ChatFormatter.of(Config.getString("msg.message_receiver"))
                    .replace(ChatFormatter.K_PLAYER, senderName)
                    .replace(ChatFormatter.K_TARGET, targetName)
                    .replace(ChatFormatter.K_MESSAGE, msg);
                player.sendMessage(formatter.build());
            }

            return Command.SINGLE_SUCCESS;
        });
        return message;
    }
}
