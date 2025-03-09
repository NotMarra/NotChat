package com.notmarra.notchat;

import com.notmarra.notchat.cmds.Chat;
import com.notmarra.notchat.listeners.ChatListener;
import com.notmarra.notchat.utils.commandHandler.NotCommand;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static com.notmarra.notchat.cmds.Chat.Msg;

public final class NotChat extends JavaPlugin {
    public static NotChat instance;

    @Override
    public void onEnable() {
        instance = this;
        // Load configuration
        this.saveDefaultConfig();

        // Register events
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            List<NotCommand> msgCommands = Msg();
            for (NotCommand cmd : msgCommands) {
                commands.registrar().register(cmd.build());
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static NotChat getInstance() {
        return instance;
    }
}
