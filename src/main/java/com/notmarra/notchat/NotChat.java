package com.notmarra.notchat;

import com.notmarra.notchat.cmds.GamesCommandGroup;
import com.notmarra.notchat.cmds.MessageCommandGroup;
import com.notmarra.notchat.listeners.chat.modules.LocalChatListener;
import com.notmarra.notchat.listeners.chat.modules.PingChatListener;
import com.notmarra.notchat.listeners.chat.MainChatListener;
import com.notmarra.notchat.listeners.chat.modules.ColorChatInvListener;
import com.notmarra.notchat.listeners.chat.modules.FilterChatListener;
import com.notmarra.notchat.listeners.chat.modules.FormatChatListener;
import com.notmarra.notchat.listeners.chat.modules.TabChatListener;
import com.notmarra.notchat.listeners.chat.modules.WorldListener;
import com.notmarra.notlib.extensions.NotPlugin;

import net.milkbowl.vault.permission.Permission;

public final class NotChat extends NotPlugin {
    private static NotChat instance;
    private static Permission perms;

    @Override
    public void initNotPlugin() {
        // listeners
        addListener(new FilterChatListener(this));
        addListener(new FormatChatListener(this));
        addListener(new LocalChatListener(this));
        addListener(new PingChatListener(this));
        addListener(new TabChatListener(this));
        addListener(new ColorChatInvListener(this));
        addListener(new WorldListener(this));
        // main chat listener using format, filter, and local
        addListener(new MainChatListener(this));

        // commands
        addCommandGroup(new GamesCommandGroup(this));
        addCommandGroup(new MessageCommandGroup(this));

        // plugin callbacks
        addPluginEnabledCallback("Vault", () -> {
            this.getLogger().info("Vault found, hooking into it");
            perms = getServer()
                .getServicesManager()
                .getRegistration(Permission.class)
                .getProvider();
        });
    }

    @Override
    public void onEnable() {
        instance = this;
        super.onEnable();
        this.getLogger().info("Enabled (Version: " + this.getPluginMeta().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.getLogger().info("Disabled (Version: " + this.getPluginMeta().getVersion() + ")");
    }

    public FilterChatListener getFilterChatListener() { return (FilterChatListener) getListener(FilterChatListener.ID); }
    public FormatChatListener getFormatChatListener() { return (FormatChatListener) getListener(FormatChatListener.ID); }
    public LocalChatListener getLocalChatListener() { return (LocalChatListener) getListener(LocalChatListener.ID); }
    public PingChatListener getPingChatListener() { return (PingChatListener) getListener(PingChatListener.ID); }
    public TabChatListener getTabChatListener() { return (TabChatListener) getListener(TabChatListener.ID); }
    public ColorChatInvListener getColorChatInvListener() { return (ColorChatInvListener) getListener(ColorChatInvListener.ID); }
    public WorldListener getWorldListener() { return (WorldListener) getListener(WorldListener.ID); }

    public static NotChat getInstance() { return instance; }
    public static Permission getPerms() { return perms; }
}