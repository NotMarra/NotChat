package com.notmarra.notchat.utils;

import com.notmarra.notchat.NotChat;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ConfigFiles {
    public static String getString(String path, String string) {
        File file = new File(NotChat.getInstance().getDataFolder() + path);
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if(yamlConfiguration.contains(string)) {
            return yamlConfiguration.getString(string);
        } else {
            return null;
        }
    }

    public static List<String> getStringList(String path, String string) {
        File file = new File(NotChat.getInstance().getDataFolder() + path);
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if(yamlConfiguration.contains(string)) {
            return yamlConfiguration.getStringList(string);
        } else {
            return null;
        }
    }

    public static ConfigurationSection getConfigurationSection(String path, String string) {
        File file = new File(NotChat.getInstance().getDataFolder() + path);
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if(yamlConfiguration.contains(string)) {
            return yamlConfiguration.getConfigurationSection(string);
        } else {
            return null;
        }
    }

    public static void createFile(String name) {
        File file = new File(NotChat.getInstance().getDataFolder().getAbsolutePath(), name);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            NotChat.getInstance().saveResource(name, false);
        }

        try {
            Reader reader = new InputStreamReader(NotChat.getInstance().getResource(name));
            YamlConfiguration.loadConfiguration(reader);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createFileAs(String source, String name) {
        File targetFile = new File(NotChat.getInstance().getDataFolder(), name);
        if (!targetFile.exists()) {
            targetFile.getParentFile().mkdirs();
            try (InputStream inputStream = NotChat.getInstance().getResource(source)) {
                if (inputStream != null) {
                    java.nio.file.Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    NotChat.getInstance().getLogger().warning("Resource not found: " + source);
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            Reader reader = new InputStreamReader(NotChat.getInstance().getResource(name));
            YamlConfiguration.loadConfiguration(reader);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
