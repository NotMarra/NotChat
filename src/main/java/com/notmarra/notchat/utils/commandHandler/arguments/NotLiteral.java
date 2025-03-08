package com.notmarra.notchat.utils.commandHandler.arguments;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class NotLiteral extends NotArgument {

    public NotLiteral(String name) {
        super(name);
    }

    public LiteralArgumentBuilder<CommandSourceStack> construct(String id) {
        return Commands.literal(id);
    }

    @Override
    public String get(CommandContext ctx, String id) {
        return id;
    }
}
