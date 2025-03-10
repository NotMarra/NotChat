package com.notmarra.notchat.utils.commandHandler.arguments;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class NotLiteral extends NotArgument<String> {

    public NotLiteral(String name) {
        super(name);
    }

    public LiteralArgumentBuilder<CommandSourceStack> construct() {
        return Commands.literal(this.name);
    }

    @Override
    public String get(CommandContext<CommandSourceStack> ctx) {
        return this.name;
    }
}
