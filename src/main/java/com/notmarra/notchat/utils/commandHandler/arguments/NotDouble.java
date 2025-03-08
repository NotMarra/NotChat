package com.notmarra.notchat.utils.commandHandler.arguments;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import javax.annotation.Nullable;

public class NotDouble extends NotArgument {
    @Nullable private Double min;
    @Nullable private Double max;

    public NotDouble(String name) {
        super(name);
    }

    public NotDouble setMin(double min) {
        this.min = min;
        return this;
    }

    public NotDouble setMax(double max) {
        this.max = max;
        return this;
    }

    public NotDouble setRange(double min, double max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public RequiredArgumentBuilder<CommandSourceStack, Double> construct(String id) {
        if (this.min != null && this.max != null) {
            return Commands.argument(id, DoubleArgumentType.doubleArg(this.min, this.max));
        } else if (this.min != null) {
            return Commands.argument(id, DoubleArgumentType.doubleArg(this.min));
        } else if (this.max != null) {
            return Commands.argument(id, DoubleArgumentType.doubleArg(this.max));
        } else {
            return Commands.argument(id, DoubleArgumentType.doubleArg());
        }
    }

    @Override
    public Double get(CommandContext ctx, String id) {
        return DoubleArgumentType.getDouble(ctx, id);
    }
}
