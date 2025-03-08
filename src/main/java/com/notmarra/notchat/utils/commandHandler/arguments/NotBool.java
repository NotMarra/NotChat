package com.notmarra.notchat.utils.commandHandler.arguments;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class NotBool extends NotArgument {

    public NotBool(String name) {
        super(name);
    }

    public RequiredArgumentBuilder<CommandSourceStack, Boolean> construct(String id) {
        return Commands.argument(id, BoolArgumentType.bool());
    }

    @Override
    public Boolean get(CommandContext ctx, String id) {
        return BoolArgumentType.getBool(ctx, id);
    }
}
