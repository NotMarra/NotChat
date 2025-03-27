package com.notmarra.notchat.listeners;

import com.notmarra.notchat.NotChat;
import com.notmarra.notlib.extensions.NotCommandGroup;

public abstract class NotChatCommandGroup extends NotCommandGroup {
    public NotChatCommandGroup(NotChat plugin) { super(plugin); }

    public abstract String getId();

    private String getModulePath() { return "modules." + getId(); }

    @Override
    public String getConfigPath() { return "modules/" + getId() + ".yml"; }

    @Override
    public boolean isEnabled() {
        return getPluginConfig().getBoolean(getModulePath());
    }
}