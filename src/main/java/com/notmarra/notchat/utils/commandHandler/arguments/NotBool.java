package com.notmarra.notchat.utils.commandHandler.arguments;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class NotBool extends NotArgument<Boolean> {

    public NotBool(String name) {
        super(name);
    }

    public RequiredArgumentBuilder<CommandSourceStack, Boolean> construct() {
        return Commands.argument(this.name, BoolArgumentType.bool());
    }

    @Override
    public Boolean get(CommandContext<CommandSourceStack> ctx) {
        return BoolArgumentType.getBool(ctx, this.name);
    }
}
