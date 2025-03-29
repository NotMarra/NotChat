package com.notmarra.notchat.listeners;

import java.util.List;

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

    public String getModuleConfigPath() { return "modules/" + getId() + ".yml"; }

    @Override
    public List<String> getConfigPaths() {
        if (isModule()) return List.of(getModuleConfigPath());
        return List.of();
    }

    @Override
    public boolean isEnabled() {
        if (isModule()) return getPluginConfig().getBoolean(getModulePath());
        return true;
    }
}