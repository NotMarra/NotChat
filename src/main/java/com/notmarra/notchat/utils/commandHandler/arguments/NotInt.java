package com.notmarra.notchat.utils.commandHandler.arguments;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import javax.annotation.Nullable;

public class NotInt extends NotArgument {
    @Nullable private Integer min;
    @Nullable private Integer max;

    public NotInt(String name) {
        super(name);
    }

    public NotInt setMin(int min) {
        this.min = min;
        return this;
    }

    public NotInt setMax(int max) {
        this.max = max;
        return this;
    }

    public NotInt setRange(int min, int max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public RequiredArgumentBuilder<CommandSourceStack, Integer> construct(String id) {
        if (this.min != null && this.max != null) {
            return Commands.argument(id, IntegerArgumentType.integer(this.min, this.max));
        } else if (this.min != null) {
            return Commands.argument(id, IntegerArgumentType.integer(this.min));
        } else if (this.max != null) {
            return Commands.argument(id, IntegerArgumentType.integer(this.max));
        } else {
            return Commands.argument(id, IntegerArgumentType.integer());
        }
    }

    @Override
    public Integer get(CommandContext ctx, String id) {
        return IntegerArgumentType.getInteger(ctx, id);
    }
}
