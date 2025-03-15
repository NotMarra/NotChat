package com.notmarra.notchat.utils;

import com.notmarra.notchat.NotChat;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class Config {
    static NotChat instance = NotChat.getInstance();

    public static String getString(String key) {
        return instance.getConfig().getString(key);
    }

    public static int getInt(String key) {
        return instance.getConfig().getInt(key);
    }

    public static boolean getBoolean(String key) {
        return instance.getConfig().getBoolean(key);
    }

    public static double getDouble(String key) {
        return instance.getConfig().getDouble(key);
    }

    public static List<String> getStringList(String key) {
        return instance.getConfig().getStringList(key);
    }

    public static ConfigurationSection getConfigurationSection(String key) {
        return instance.getConfig().getConfigurationSection(key);
    }

    public void save() {
        instance.saveConfig();
    }

    public void reload() {
        instance.reloadConfig();
    }

    public void set(String key, Object value) {
        instance.getConfig().set(key, value);
    }
}
