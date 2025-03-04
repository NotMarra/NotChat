package com.notmarra.notchat;

import com.notmarra.notchat.cmds.Msg;
import com.notmarra.notchat.listeners.ChatListener;
import org.bukkit.plugin.java.JavaPlugin;
import com.notmarra.notchat.utils.ChatFormatter;
import com.notmarra.notchat.utils.ChatManager;

public final class NotChat extends JavaPlugin {

    @Override
    public void onEnable() {
        // Load configuration
        this.saveDefaultConfig();

        // Register commands
        this.getCommand("msg").setExecutor(new Msg(this));

        // Register events
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
