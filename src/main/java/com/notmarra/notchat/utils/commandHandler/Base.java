package com.notmarra.notchat.utils.commandHandler;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.notmarra.notchat.utils.commandHandler.arguments.NotArgument;

import io.papermc.paper.command.brigadier.CommandSourceStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public abstract class Base {
    public final String name;
    public HashMap<String, NotArgument<?>> arguments = new HashMap<>();
    public Function<CommandContext<CommandSourceStack>,Integer> executor = (ctx) -> 1;
    public List<String> suggestions = List.of();
    @Nullable public String permission;


    public Base(String name) {
        this.name = name;
    }

    public Base addArg(NotArgument<?> arg) {
        this.arguments.put(arg.name, arg);
        return this;
    }

    public Base onExecute(Function<CommandContext<CommandSourceStack>, Integer> executor) {
        this.executor = executor;
        return this;
    }

    public Base setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
        return this;
    }

    public Base setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public @Nullable String getPermission() {
        return this.permission;
    }

    public abstract CommandNode<CommandSourceStack> build();
}
