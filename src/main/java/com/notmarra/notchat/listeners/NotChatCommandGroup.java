package com.notmarra.notchat.listeners;

import java.util.List;

import com.notmarra.notlib.extensions.NotCommandGroup;
import com.notmarra.notlib.extensions.NotPlugin;

public abstract class NotChatCommandGroup extends NotCommandGroup {
    public NotChatCommandGroup(NotPlugin plugin) { super(plugin); }

    public abstract String getId();

    private String getModulePath() { return "modules." + getId(); }

    public String getModuleConfigPath() { return "modules/" + getId() + ".yml"; }

    @Override
    public List<String> getConfigPaths() { return List.of(getModuleConfigPath()); }

    @Override
    public boolean isEnabled() {
        return getPluginConfig().getBoolean(getModulePath());
    }
}