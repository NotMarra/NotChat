package org.example.notchat;

import org.bukkit.plugin.java.JavaPlugin;
import org.example.notchat.utils.ChatFormatter;
import org.example.notchat.utils.ChatManager;

public final class NotChat extends JavaPlugin {
    private ChatManager chatManager;
    private ChatFormatter chatFormatter;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        chatManager = new ChatManager();
        chatFormatter = new ChatFormatter();

        loadChatGroups();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadChatGroups() {
        if (getConfig().contains("groups")) {
            for (String groupName : getConfig().getConfigurationSection("groups").getKeys(false)) {
                String format = getConfig().getString("groups." + groupName + ".format");
                String permission = getConfig().getString("groups." + groupName + ".permission");
                boolean isDefault = getConfig().getBoolean("groups." + groupName + ".default", false);

                chatManager.registerGroup(groupName, format, permission, isDefault);
            }
        }
    }
}
