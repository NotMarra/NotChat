package com.notmarra.notchat;

import com.notmarra.notchat.listeners.ChatListener;
import com.notmarra.notchat.utils.Config;
import com.notmarra.notchat.utils.commandHandler.NotCommand;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

import static com.notmarra.notchat.cmds.Chat.Msg;

public final class NotChat extends JavaPlugin {
    public static NotChat instance;
    public static Boolean Vault = false;
    public static HashMap<String, String> chat_formats = new HashMap<>();
    private static Permission perms = null;


    @Override
    public void onEnable() {
        instance = this;
        // Load configuration
        this.saveDefaultConfig();

        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            Vault = true;
            this.getLogger().info("Vault found, hooking into it");

            RegisteredServiceProvider<Permission> rsp = NotChat.getInstance().getServer().getServicesManager().getRegistration(Permission.class);
            perms = rsp.getProvider();
        }

        // Register events
        if (Config.getBoolean("modules.chat_format")) {
            ConfigurationSection formats = Config.getConfigurationSection("chat_formats");
            for (String key : formats.getKeys(false)) {
                chat_formats.put(key, formats.getString(key));
            }
            this.getServer().getPluginManager().registerEvents(new ChatListener(this), this);
            this.getLogger().info("Chat format module enabled");
        }

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            if (Config.getBoolean("modules.msg")) {
                List<NotCommand> msgCommands = Msg();
                for (NotCommand cmd : msgCommands) {
                    commands.registrar().register(cmd.build());
                }
                this.getLogger().info("Msg commands registered");
            }
        });


        this.getLogger().info("Enabled successfully - Version: " + this.getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.getLogger().info("Disabled successfully");
    }

    public static NotChat getInstance() {
        return instance;
    }

    public static Boolean hasVault() {
        return Vault;
    }

    public static HashMap<String, String> getChatFormats() {
        return chat_formats;
    }

    public static Permission getPerms() {
        return perms;
    }
}