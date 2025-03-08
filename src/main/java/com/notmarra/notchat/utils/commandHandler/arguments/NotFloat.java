package com.notmarra.notchat.utils.commandHandler.arguments;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import javax.annotation.Nullable;

public class NotFloat extends NotArgument{
    @Nullable private Float min;
    @Nullable private Float max;

    public NotFloat(String name) {
        super(name);
    }

    public NotFloat setMin(float min) {
        this.min = min;
        return this;
    }

    public NotFloat setMax(float max) {
        this.max = max;
        return this;
    }

    public NotFloat setRange(float min, float max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public RequiredArgumentBuilder<CommandSourceStack, Float> construct(String id) {
        if (this.min != null && this.max != null) {
            return Commands.argument(id, FloatArgumentType.floatArg(this.min, this.max));
        } else if (this.min != null) {
            return Commands.argument(id, FloatArgumentType.floatArg(this.min));
        } else if (this.max != null) {
            return Commands.argument(id, FloatArgumentType.floatArg(this.max));
        } else {
            return Commands.argument(id, FloatArgumentType.floatArg());
        }
    }

    @Override
    public Float get(CommandContext ctx, String id) {
        return FloatArgumentType.getFloat(ctx, id);
    }
}