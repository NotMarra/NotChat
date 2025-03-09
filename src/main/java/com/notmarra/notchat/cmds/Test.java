package com.notmarra.notchat.cmds;

import com.mojang.brigadier.Command;
import com.notmarra.notchat.utils.commandHandler.NotCommand;
import com.notmarra.notchat.utils.commandHandler.arguments.NotInt;
import com.notmarra.notchat.utils.commandHandler.arguments.NotString;

import java.util.List;

public class Test {
    public static NotCommand Test() {
        NotCommand cmd = new NotCommand("test");
        cmd.onExecute((ctx)-> {
            ctx.getSource().getSender().sendMessage("Si vole nezadal číslo pepege");
            return Command.SINGLE_SUCCESS;
        });
        NotInt hovno = new NotInt("hovno");
        hovno.setRange(2, 5);
        hovno.setSuggestions(List.of("2", "3", "4", "5"));
        hovno.onExecute(ctx -> {
            int test = (int) ctx.getArgument("hovno", Integer.class);
            ctx.getSource().getSender().sendMessage("Test Int: " + test);
            return Command.SINGLE_SUCCESS;
        });
        NotString hovno2 = new NotString("hovno2");
        hovno2.setSuggestions(List.of("test", "test2", "test3"));
        hovno2.onExecute(ctx -> {
            int test = (int) ctx.getArgument("hovno", Integer.class);
            String test2 = (String) ctx.getArgument("hovno2", String.class);
            ctx.getSource().getSender().sendMessage("Test String: " + test + " " + test2);
            return Command.SINGLE_SUCCESS;
        });
        hovno.addArg(hovno2);
        cmd.addArg(hovno);
        return cmd;
    }
}
