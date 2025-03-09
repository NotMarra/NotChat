package com.notmarra.notchat.utils.commandHandler.arguments;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import javax.annotation.Nullable;

public class NotLong extends NotArgument<Long> {
    @Nullable private Long min;
    @Nullable private Long max;

    public NotLong(String name) {
        super(name);
    }

    public NotLong setMin(long min) {
        this.min = min;
        return this;
    }

    public NotLong setMax(long max) {
        this.max = max;
        return this;
    }

    public NotLong setRange(long min, long max) {
        this.min = min;
        this.max = max;
        return this;
    }

    public RequiredArgumentBuilder<CommandSourceStack, Long> construct() {
        if (this.min != null && this.max != null) {
            return Commands.argument(this.name, LongArgumentType.longArg(this.min, this.max));
        } else if (this.min != null) {
            return Commands.argument(this.name, LongArgumentType.longArg(this.min));
        } else if (this.max != null) {
            return Commands.argument(this.name, LongArgumentType.longArg(this.max));
        } else {
            return Commands.argument(this.name, LongArgumentType.longArg());
        }
    }

    @Override
    public Long get(CommandContext<CommandSourceStack> ctx) {
        return LongArgumentType.getLong(ctx, this.name);
    }
}

