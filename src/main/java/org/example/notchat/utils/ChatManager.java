package org.example.notchat.utils;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChatManager {
    private final Map<String, ChatGroup> groups = new HashMap<>();
    private ChatGroup defaultGroup;

    public void registerGroup(String name, String format, String permission, boolean isDefault) {
        ChatGroup group = new ChatGroup(name, format, permission, isDefault);
        groups.put(name.toLowerCase(), group);

        if (isDefault) {
            defaultGroup = group;
        }
    }

    public ChatGroup getGroup(String name) {
        return groups.get(name.toLowerCase());
    }

    public ChatGroup getDefaultGroup() {
        return defaultGroup;
    }

    public ChatGroup getPlayerGroup(Player player) {
        for (ChatGroup group : groups.values()) {
            if (group.isMember(player.getUniqueId())) {
                return group;
            }
        }

        for (ChatGroup group : groups.values()) {
            if (group.hasPermission(player)) {
                return group;
            }
        }

        return defaultGroup;
    }

    public void setPlayerGroup(Player player, String groupName) {
        for (ChatGroup group : groups.values()) {
            group.removeMember(player.getUniqueId());
        }

        ChatGroup newGroup = getGroup(groupName);
        if (newGroup != null) {
            newGroup.addMember(player.getUniqueId());
        }
    }

    public Collection<ChatGroup> getAllGroups() {
        return groups.values();
    }
}