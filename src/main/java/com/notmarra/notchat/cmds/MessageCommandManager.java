package com.notmarra.notchat.cmds;

import com.notmarra.notchat.NotChat;
import com.notmarra.notlib.utils.ChatF;
import com.notmarra.notlib.utils.command.NotCommand;
import com.notmarra.notlib.utils.command.arguments.NotPlayersArg;

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
        NotCommand command = NotCommand.of(name, cmd -> {
            ChatF.of(messageUsage).sendTo(cmd.getPlayer());
        });

        NotPlayersArg players = command.playersArg("player", arg -> {
            ChatF.of(messageUsage).sendTo(arg.getPlayer());
        });

        players.greedyStringArg("message", arg -> {
            for (Player targetPlayer : players.get()) {
                ChatF.of(messageSender)
                    .withPlayer(arg.getPlayer())
                    .withTargetPlayer(targetPlayer)
                    .replace(ChatF.K_MESSAGE, arg.get())
                    .sendTo(arg.getPlayer());

                ChatF.of(messageReceiver)
                    .withPlayer(arg.getPlayer())
                    .withTargetPlayer(targetPlayer)
                    .replace(ChatF.K_MESSAGE, arg.get())
                    .sendTo(targetPlayer);
            }
        });

        return command;
    }
}
