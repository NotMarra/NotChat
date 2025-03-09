package com.notmarra.notchat.utils.commandHandler.arguments;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import javax.annotation.Nullable;

public class NotString extends NotArgument<String> {

    public NotString(String name) {
        super(name);
    }

    @Override
    public RequiredArgumentBuilder<CommandSourceStack, String> construct() {
        return Commands.argument(this.name, StringArgumentType.string());
    }

    @Override
    public String get(CommandContext<CommandSourceStack> ctx) {
        return StringArgumentType.getString(ctx, this.name);
    }
}