package com.notmarra.notchat.cmds;

import com.notmarra.notchat.NotChat;
import com.notmarra.notlib.utils.command.NotCommand;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.logging.Logger;

public abstract class BaseNotCommandManager {
    public final NotChat plugin;
    public FileConfiguration config;

    private boolean isInitialized = false;
    private boolean isRegistered = false;

    public BaseNotCommandManager(NotChat plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        if (isInitialized) return;
        isInitialized = true;
        this.config = plugin.getSubConfig(getConfigFile());
        loadConfig();
    }

    public abstract List<NotCommand> buildCommands();

    public abstract boolean hasConfig();

    public void loadConfig() {}

    public abstract String getId();

    public String getConfigFile() {
        return getId() + ".yml";
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

    public void register() {
        if (isRegistered) {
            return;
        }

        initialize();

        String modulePath = getModulePath();

        if (!getPluginConfig().isBoolean(modulePath)) {
            getLogger().warning("Module " + getId() + " has missing configuration '" + modulePath + "'!");
            return;
        }

        if (!getPluginConfig().getBoolean(modulePath)) return;

        this.getLogger().info(getId().toUpperCase() + " module enabled");

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            for (NotCommand cmd : buildCommands()) {
                commands.registrar().register(cmd.build());
            }
        });
    }
}