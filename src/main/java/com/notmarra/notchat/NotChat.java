package com.notmarra.notchat;

import com.notmarra.notchat.cmds.BaseNotCommandManager;
import com.notmarra.notchat.cmds.GamesCommandManager;
import com.notmarra.notchat.cmds.MessageCommandManager;
import com.notmarra.notchat.listeners.chat.modules.LocalChatListener;
import com.notmarra.notchat.listeners.chat.modules.PingChatListener;
import com.notmarra.notchat.listeners.chat.MainChatListener;
import com.notmarra.notchat.listeners.BaseNotListener;
import com.notmarra.notchat.listeners.chat.modules.FilterChatListener;
import com.notmarra.notchat.listeners.chat.modules.FormatChatListener;
import com.notmarra.notchat.listeners.chat.modules.TabChatListener;
import com.notmarra.notchat.listeners.chat.modules.WorldListener;
import com.notmarra.notchat.listeners.inventory.ColorChatInvListener;
import com.notmarra.notchat.utils.MinecraftStuff;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class NotChat extends JavaPlugin {
    private static NotChat instance;
    private static Boolean Vault = false;
    private static Boolean PlaceholderAPI = false;
    private static Permission perms = null;

    private static final Map<String, FileConfiguration> CONFIGS = new HashMap<>();
    private static final Map<String, BaseNotListener> LISTENERS = new HashMap<>();
    private static final Map<String, BaseNotCommandManager> CMDMGRS = new HashMap<>();

    private void loadConfigFiles() {
        CONFIGS.put("config.yml", this.getConfig());

        for (BaseNotListener listener : LISTENERS.values()) {
            reloadConfig(listener.getConfigFile());
        }

        for (BaseNotCommandManager cmdGroup : CMDMGRS.values()) {
            reloadConfig(cmdGroup.getConfigFile());
        }
    }

    private void initListeners() {
        LISTENERS.put(FilterChatListener.ID, new FilterChatListener(this));
        LISTENERS.put(FormatChatListener.ID, new FormatChatListener(this));
        LISTENERS.put(LocalChatListener.ID, new LocalChatListener(this));
        LISTENERS.put(PingChatListener.ID, new PingChatListener(this));
        LISTENERS.put(TabChatListener.ID, new TabChatListener(this));
        LISTENERS.put(ColorChatInvListener.ID, new ColorChatInvListener(this));
        LISTENERS.put(WorldListener.ID, new WorldListener(this));

        // main chat listener using format, filter, and local
        LISTENERS.put(MainChatListener.ID, new MainChatListener(this));
    }

    private void initCommandGroups() {
        CMDMGRS.put(GamesCommandManager.ID, new GamesCommandManager(this));
        CMDMGRS.put(MessageCommandManager.ID, new MessageCommandManager(this));
    }

    @Override
    public void saveDefaultConfig() {
        super.saveDefaultConfig();

        for (BaseNotListener listener : LISTENERS.values()) {
            File configFile = new File(getDataFolder(), listener.getConfigFile());
            if (!configFile.exists() && listener.hasConfig()) {
                saveResource(listener.getConfigFile(), false);
            }
        }

        for (BaseNotCommandManager cmdGroup : CMDMGRS.values()) {
            File configFile = new File(getDataFolder(), cmdGroup.getConfigFile());
            if (!configFile.exists() && cmdGroup.hasConfig()) {
                saveResource(cmdGroup.getConfigFile(), false);
            }
        }
    }

    @Override
    public void onEnable() {
        MinecraftStuff.getInstance().initialize();

        instance = this;

        // NOTE: this order is important
        this.initListeners();
        this.initCommandGroups();
        this.saveDefaultConfig();
        this.loadConfigFiles();

        // Check PlaceholderAPI
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.getLogger().info("PlaceholderAPI found, hooking into it");
            PlaceholderAPI = true;
        }

        // Check Vault
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            Vault = true;
            this.getLogger().info("Vault found, hooking into it");

            RegisteredServiceProvider<Permission> rsp = getServer()
                .getServicesManager()
                .getRegistration(Permission.class);
            perms = rsp.getProvider();
        }

        LISTENERS.values().forEach(l -> l.register());
        CMDMGRS.values().forEach(c -> c.register());

        this.getLogger().info("Enabled successfully - Version: " + this.getPluginMeta().getVersion());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.getLogger().info("Disabled successfully");
    }

    public static NotChat getInstance() {
        return instance;
    }

    public static Boolean hasVault() {
        return Vault;
    }

    public static Boolean hasPAPI() { return PlaceholderAPI; }

    public static Permission getPerms() {
        return perms;
    }

    public FileConfiguration getSubConfig(String file) {
        return CONFIGS.get(file);
    }

    public void reloadConfig(String file) {
        File configFile = new File(getDataFolder(), file);
        if (!configFile.exists()) return;
        CONFIGS.put(file, YamlConfiguration.loadConfiguration(configFile));
    }

    public FilterChatListener getFilterChatListener() { return (FilterChatListener) LISTENERS.get(FilterChatListener.ID); }
    public FormatChatListener getFormatChatListener() { return (FormatChatListener) LISTENERS.get(FormatChatListener.ID); }
    public LocalChatListener getLocalChatListener() { return (LocalChatListener) LISTENERS.get(LocalChatListener.ID); }
    public PingChatListener getPingChatListener() { return (PingChatListener) LISTENERS.get(PingChatListener.ID); }
    public TabChatListener getTabChatListener() { return (TabChatListener) LISTENERS.get(TabChatListener.ID); }
    public ColorChatInvListener getColorChatInvListener() { return (ColorChatInvListener) LISTENERS.get(ColorChatInvListener.ID); }
    public WorldListener getWorldListener() { return (WorldListener) LISTENERS.get(WorldListener.ID); }
}