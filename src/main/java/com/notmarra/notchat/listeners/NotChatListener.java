package com.notmarra.notchat.listeners;

import com.notmarra.notchat.NotChat;
import com.notmarra.notlib.extensions.NotListener;

public abstract class NotChatListener extends NotListener {
    // NOTE: overrides the NotPlugin instance variable
    public final NotChat plugin;

    public NotChatListener(NotChat plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    public abstract String getId();

    public boolean isModule() { return true; }

    private String getModulePath() { return "modules." + getId(); }

    @Override
    public String getConfigPath() {
        if (isModule()) return "modules/" + getId() + ".yml";
        return null;
    }

    @Override
    public boolean isEnabled() {
        if (isModule()) return getPluginConfig().getBoolean(getModulePath());
        return true;
    }
}