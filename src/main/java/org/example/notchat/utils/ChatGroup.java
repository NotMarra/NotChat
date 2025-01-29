package org.example.notchat.utils;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChatGroup {
    private final String name;
    private final String format;
    private final String permission;
    private final Set<UUID> members;
    private final boolean isDefault;

    public ChatGroup(String name, String format, String permission, boolean isDefault) {
        this.name = name;
        this.format = format;
        this.permission = permission;
        this.isDefault = isDefault;
        this.members = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public String getFormat() {
        return format;
    }

    public boolean hasPermission(Player player) {
        return permission == null || permission.isEmpty() || player.hasPermission(permission);
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void addMember(UUID uuid) {
        members.add(uuid);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public boolean isMember(UUID uuid) {
        return members.contains(uuid);
    }

    public Set<UUID> getMembers() {
        return new HashSet<>(members);
    }
}
