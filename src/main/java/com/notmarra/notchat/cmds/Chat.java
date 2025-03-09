package com.notmarra.notchat.cmds;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.notmarra.notchat.utils.commandHandler.NotCommand;
import com.notmarra.notchat.utils.commandHandler.arguments.NotEntity;
import com.notmarra.notchat.utils.commandHandler.arguments.NotString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.List;

public class Chat {
    public static NotCommand Msg() {
        NotCommand cmd = new NotCommand("msg");
        cmd.onExecute((ctx)-> {
            ctx.getSource().getSender().sendMessage("Nezadal si zprávu");
            return Command.SINGLE_SUCCESS;
        });
        NotEntity players = new NotEntity("player");
        List<String> suggestions = Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).toList();
        players.setSuggestions(suggestions);
        players.onExecute(ctx -> {
            List<Entity> player = players.get(ctx);
            player.get(0).sendMessage("Test");
            return Command.SINGLE_SUCCESS;
        });
        NotString message = new NotString("message");
        message.onExecute(ctx -> {
            List<Entity> player = players.get(ctx);
            String msg = message.get(ctx);
            ctx.getSource().getSender().sendMessage("Test String: " + msg);
            for (Entity p : player) {
                p.sendMessage(msg);
            }
            return Command.SINGLE_SUCCESS;
        });
        players.addArg(message);
        cmd.addArg(players);
        return cmd;
    }
}
