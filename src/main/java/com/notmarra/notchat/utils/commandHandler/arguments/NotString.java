package com.notmarra.notchat.utils.commandHandler.arguments;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import javax.annotation.Nullable;

public class NotString extends NotArgument {

    public NotString(String name) {
        super(name);
    }

    @Override
    public RequiredArgumentBuilder<CommandSourceStack, String> construct(String id) {
        return Commands.argument(id, StringArgumentType.string());
    }

    @Override
    public String get(CommandContext ctx, String id) {
        return StringArgumentType.getString(ctx, id);
    }
}