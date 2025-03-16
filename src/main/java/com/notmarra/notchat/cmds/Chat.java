package com.notmarra.notchat.cmds;

import com.mojang.brigadier.Command;
import com.notmarra.notchat.utils.ChatF;
import com.notmarra.notchat.utils.Config;
import com.notmarra.notchat.utils.commandHandler.NotCommand;
import com.notmarra.notchat.utils.commandHandler.arguments.NotPlayerArg;
import com.notmarra.notchat.utils.commandHandler.arguments.NotStringArg;

import org.bukkit.entity.Entity;
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
                ChatF message = ChatF.of(Config.getString("msg.message_usage"));
                ctx.getSource().getSender().sendMessage(message.build());
                return Command.SINGLE_SUCCESS;
            });

            NotPlayerArg players = new NotPlayerArg("player");
            players.onExecute(ctx -> {
                ChatF message = ChatF.of(Config.getString("msg.message_usage"));
                ctx.getSource().getSender().sendMessage(message.build());
                return Command.SINGLE_SUCCESS;
            });

            NotStringArg message = new NotStringArg("message");
            message.onExecute(ctx -> {
                List<Player> targetPlayers = players.get(ctx);
                String msg = message.get(ctx);
                Entity senderEntity = ctx.getSource().getExecutor();

                if (!(senderEntity instanceof Player)) return Command.SINGLE_SUCCESS;

                for (Player targetPlayer : targetPlayers) {
                    // Send message to sender
                    ChatF senderMessage = ChatF.of(Config.getString("msg.message_sender"))
                        .withPlayer((Player) senderEntity)
                        .withTargetPlayer(targetPlayer)
                        .replace(ChatF.K_MESSAGE, msg);
                    senderEntity.sendMessage(senderMessage.build());

                    // Send message to receiver
                    ChatF formatter = ChatF.of(Config.getString("msg.message_receiver"))
                        .withPlayer((Player) senderEntity)
                        .withTargetPlayer(targetPlayer)
                        .replace(ChatF.K_MESSAGE, msg);
                    targetPlayer.sendMessage(formatter.build());
                }

                return Command.SINGLE_SUCCESS;
            });

            players.addArg(message);
            cmd.addArg(players);
            commands.add(cmd);
        }

        return commands;
    }
}
