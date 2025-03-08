package com.notmarra.notchat.utils.commandHandler.arguments;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public abstract class NotArgument extends Base {

    public NotArgument(String name) {
        super(name);
    }

    public abstract ArgumentBuilder<CommandSourceStack, ?> construct(String id);

    public abstract <T> T get(CommandContext ctx, String id);


    public CommandNode<CommandSourceStack> build() {
        ArgumentBuilder<CommandSourceStack, ?> cmd = construct(this.name);
        for (String arg : this.arguments.keySet()) {
            cmd = cmd.then(this.arguments.get(arg).build());
        }
        cmd = cmd.requires(source -> {
            if (this.permission != null) {
                return source.getSender().hasPermission(this.permission);
            }
            return true;
        });
        cmd = cmd.executes(context -> {
            executor.apply(context);
            return 1;
        });
        return cmd.build();
    }
}

