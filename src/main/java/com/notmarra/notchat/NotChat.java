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
        addListener(FilterChatListener.ID, new FilterChatListener(this));
        addListener(FormatChatListener.ID, new FormatChatListener(this));
        addListener(LocalChatListener.ID, new LocalChatListener(this));
        addListener(PingChatListener.ID, new PingChatListener(this));
        addListener(TabChatListener.ID, new TabChatListener(this));
        addListener(ColorChatInvListener.ID, new ColorChatInvListener(this));
        addListener(WorldListener.ID, new WorldListener(this));
        // main chat listener using format, filter, and local
        addListener(MainChatListener.ID, new MainChatListener(this));

        // commands
        addCommandGroup(GamesCommandGroup.ID, new GamesCommandGroup(this));
        addCommandGroup(MessageCommandGroup.ID, new MessageCommandGroup(this));

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
        this.getLogger().info("Enabled successfully - Version: " + this.getPluginMeta().getVersion());
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