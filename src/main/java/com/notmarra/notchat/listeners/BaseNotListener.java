package com.notmarra.notchat.listeners;

import com.notmarra.notchat.NotChat;
import com.notmarra.notlib.utils.command.NotCommand;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

public abstract class BaseNotListener implements Listener {
    public final NotChat plugin;
    public FileConfiguration config;

    private boolean isInitialized = false;
    private boolean isRegistered = false;

    public BaseNotListener(NotChat plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        if (isInitialized) return;
        isInitialized = true;
        this.config = plugin.getSubConfig(getConfigFile());
        loadConfig();
    }

    public boolean isModule() {
        return true;
    }

    public List<NotCommand> getNotCommands() {
        return List.of();
    }

    public abstract boolean hasConfig();
    
    public void loadConfig() {}

    public abstract String getId();

    public String getConfigFile() {
        return "modules/" + getId() + ".yml";
    }

    private String getModulePath() {
        return "modules." + getId();
    }

    public boolean isEnabled() {
        return getPluginConfig().getBoolean(getModulePath());
    }

    public void reloadConfig() {
        plugin.reloadConfig(getConfigFile());
        this.config = plugin.getSubConfig(getConfigFile());
        loadConfig();
    }

    public FileConfiguration getPluginConfig() {
        return plugin.getConfig();
    }

    public Logger getLogger() {
        return plugin.getLogger();
    }

    public BaseNotListener getListener(String id) {
        return plugin.getListener(id);
    }

    public void register() {
        if (isRegistered) return;
        isRegistered = true;

        initialize();

        if (isModule()) {
            String modulePath = getModulePath();
            if (!getPluginConfig().isBoolean(modulePath)) {
                getLogger().warning("Module " + getId() + " has missing configuration '" + modulePath + "'!");
                return;
            }

            if (!getPluginConfig().getBoolean(modulePath)) return;
        }

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.getLogger().info(getId().toUpperCase() + " module enabled");

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            for (NotCommand cmd : getNotCommands()) {
                commands.registrar().register(cmd.build());
            }
        });
    }
}