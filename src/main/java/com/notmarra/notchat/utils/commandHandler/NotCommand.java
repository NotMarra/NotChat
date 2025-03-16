package com.notmarra.notchat.utils.commandHandler;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.notmarra.notchat.utils.commandHandler.arguments.NotArgument;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.HashMap;

public class NotCommand extends Base {

    public NotCommand(String name) {
        super(name);
    }

    public NotCommand setArgs(HashMap<String, NotArgument<?>> args) {
        this.arguments = args;
        return this;
    }

    public LiteralCommandNode<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> cmd = Commands.literal(this.name);
        for (String arg : this.arguments.keySet()) {
            cmd = cmd.then(this.arguments.get(arg).build());
        }
        cmd = cmd.requires(source -> {
            if (this.permission != null) {
                return source.getSender().hasPermission(this.permission);
            }
            return true;
        });
        cmd = cmd.executes(context -> executor.apply(context));
        return cmd.build();
    }

}
