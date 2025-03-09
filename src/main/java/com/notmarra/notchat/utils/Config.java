package com.notmarra.notchat.utils;

import com.notmarra.notchat.NotChat;

import java.util.List;

public class Config {
    public static String getString(String key) {
        return NotChat.getInstance().getConfig().getString(key);
    }

    public static int getInt(String key) {
        return NotChat.getInstance().getConfig().getInt(key);
    }

    public static boolean getBoolean(String key) {
        return NotChat.getInstance().getConfig().getBoolean(key);
    }

    public static double getDouble(String key) {
        return NotChat.getInstance().getConfig().getDouble(key);
    }

    public static List<String> getStringList(String key) {
        return NotChat.getInstance().getConfig().getStringList(key);
    }

    public void save() {
        NotChat.getInstance().saveConfig();
    }

    public void reload() {
        NotChat.getInstance().reloadConfig();
    }

    public void set(String key, Object value) {
        NotChat.getInstance().getConfig().set(key, value);
    }
}
