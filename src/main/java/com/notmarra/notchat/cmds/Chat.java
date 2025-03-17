package com.notmarra.notchat.cmds;

import com.mojang.brigadier.Command;
import com.notmarra.notlib.utils.ChatF;
import com.notmarra.notchat.utils.Config;
import com.notmarra.notlib.utils.command.NotCommand;
import com.notmarra.notlib.utils.command.arguments.NotPlayerArg;
import com.notmarra.notlib.utils.command.arguments.NotStringArg;

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
                Entity entity = ctx.getSource().getExecutor();
                ChatF.of(Config.getString("msg.message_usage")).sendTo(entity);
                return Command.SINGLE_SUCCESS;
            });

            NotPlayerArg players = new NotPlayerArg("player");
            players.onExecute(ctx -> {
                Entity entity = ctx.getSource().getExecutor();
                ChatF.of(Config.getString("msg.message_usage")).sendTo(entity);
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
                    ChatF.of(Config.getString("msg.message_sender"))
                        .withPlayer((Player) senderEntity)
                        .withTargetPlayer(targetPlayer)
                        .replace(ChatF.K_MESSAGE, msg)
                        .sendTo(senderEntity);

                    // Send message to receiver
                    ChatF.of(Config.getString("msg.message_receiver"))
                        .withPlayer((Player) senderEntity)
                        .withTargetPlayer(targetPlayer)
                        .replace(ChatF.K_MESSAGE, msg)
                        .sendTo(targetPlayer);
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
