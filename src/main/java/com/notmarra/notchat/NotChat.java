package com.notmarra.notchat;

import com.notmarra.notchat.cmds.Chat;
import com.notmarra.notchat.cmds.Test;
import com.notmarra.notchat.listeners.ChatListener;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

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
            commands.registrar().register(Test.Test().build());
            commands.registrar().register(Chat.Msg().build());
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
