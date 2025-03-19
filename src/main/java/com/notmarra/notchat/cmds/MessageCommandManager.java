package com.notmarra.notchat.cmds;

import com.notmarra.notchat.NotChat;
import com.notmarra.notlib.utils.ChatF;
import com.notmarra.notlib.utils.command.NotCommand;
import com.notmarra.notlib.utils.command.arguments.NotPlayerArg;
import com.notmarra.notlib.utils.command.arguments.NotStringArg;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MessageCommandManager extends BaseNotCommandManager {
    public static final String ID = "msg";

    private String messageReceiver;
    private String messageSender;
    private String messageUsage;
    private List<String> aliases;

    public MessageCommandManager(NotChat plugin) {
        super(plugin);
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
        messageReceiver = config.getString("message_receiver");
        messageSender = config.getString("message_sender");
        messageUsage = config.getString("message_usage");
        aliases = config.getStringList("alias");
    }

    @Override
    public List<NotCommand> buildCommands() {
        List<NotCommand> commands = new ArrayList<>();

        for (String alias : aliases) {
            commands.add(messageCommand(alias));
        }

        return commands;
    }

    private NotCommand messageCommand(String name) {
        NotCommand cmd = new NotCommand(name);

        cmd.onExecute((ctx) -> {
            Entity entity = ctx.getSource().getExecutor();
            ChatF.of(messageUsage).sendTo(entity);
        });

        NotPlayerArg players = new NotPlayerArg("player");
        players.onExecute(ctx -> {
            Entity entity = ctx.getSource().getExecutor();
            ChatF.of(messageUsage).sendTo(entity);
        });

        NotStringArg message = new NotStringArg("message");
        message.onExecute(ctx -> {
            List<Player> targetPlayers = players.get(ctx);
            String msg = message.get(ctx);
            Entity senderEntity = ctx.getSource().getExecutor();

            if (!(senderEntity instanceof Player)) return;

            for (Player targetPlayer : targetPlayers) {
                // Send message to sender
                ChatF.of(messageSender)
                    .withPlayer((Player) senderEntity)
                    .withTargetPlayer(targetPlayer)
                    .replace(ChatF.K_MESSAGE, msg)
                    .sendTo(senderEntity);

                // Send message to receiver
                ChatF.of(messageReceiver)
                    .withPlayer((Player) senderEntity)
                    .withTargetPlayer(targetPlayer)
                    .replace(ChatF.K_MESSAGE, msg)
                    .sendTo(targetPlayer);
            }
        });

        players.addArg(message);
        cmd.addArg(players);

        return cmd;
    }
}
