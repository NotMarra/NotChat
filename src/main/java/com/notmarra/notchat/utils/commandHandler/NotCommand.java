package com.notmarra.notchat.utils.commandHandler;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.notmarra.notchat.utils.commandHandler.arguments.Base;
import com.notmarra.notchat.utils.commandHandler.arguments.NotArgument;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.Function;

public class NotCommand extends Base {
    public NotCommand(String name) {
        super(name);
    }

    public NotCommand setArgs(HashMap<String, NotArgument> args) {
        this.arguments = args;
        return this;
    }

    public String getString(CommandContext<?> ctx, String id) {
        return this.arguments.get(id).get(ctx, id);
    }

    public int getInt(CommandContext<?> ctx,String id) {
        return this.arguments.get(id).get(ctx, id);
    }

    public double getDouble(CommandContext<?> ctx,String id) {
        return this.arguments.get(id).get(ctx, id);
    }

    public long getLong(CommandContext<?> ctx,String id) {
        return this.arguments.get(id).get(ctx, id);
    }

    public float getFloat(CommandContext<?> ctx,String id) {
        return this.arguments.get(id).get(ctx, id);
    }

    public boolean getBoolean(CommandContext<?> ctx,String id) {
        return this.arguments.get(id).get(ctx, id);
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
        cmd = cmd.executes(context -> {
            executor.apply(context);
            return 1;
        });
        return cmd.build();
    }

}
