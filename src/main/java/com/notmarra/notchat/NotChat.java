package com.notmarra.notchat;

import com.notmarra.notchat.games.GameManager;
import com.notmarra.notchat.listeners.ChatListener;
import com.notmarra.notchat.utils.Config;
import com.notmarra.notchat.utils.MinecraftStuff;
import com.notmarra.notchat.utils.commandHandler.NotCommand;
import com.notmarra.notchat.utils.commandHandler.arguments.NotStringArg;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static com.notmarra.notchat.cmds.Chat.Msg;

public final class NotChat extends JavaPlugin {
    public static NotChat instance;
    public static Boolean Vault = false;
    public static Boolean PlaceholderAPI = false;
    public static HashMap<String, String> chat_formats = new HashMap<>();
    private static Permission perms = null;
    private static GameManager gameManager;

    private static final List<String> CONFIG_FILES = List.of(
        "config.yml",
        "games.yml"
    );

    @Override
    public void saveDefaultConfig() {
        for (String file : CONFIG_FILES) {
            File configFile = new File(getDataFolder(), file);
            if (!configFile.exists()) {
                saveResource(file, false);
            }
        }
    }

    @Override
    public void onEnable() {
        MinecraftStuff.getInstance().initialize();

        instance = this;

        // Load configuration
        this.saveDefaultConfig();

        // Check PlaceholderAPI
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.getLogger().info("PlaceholderAPI found, hooking into it");
            PlaceholderAPI = true;
        }

        // Check Vault
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

        if (Config.getBoolean("modules.chat_games")) {
            gameManager = new GameManager(this);
            this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
                for (NotCommand cmd : gameManager.getCommands()) {
                    commands.registrar().register(cmd.build());
                }
            });
            this.getLogger().info("Games module enabled");
        }

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            if (Config.getBoolean("modules.msg")) {
                for (NotCommand cmd : Msg()) {
                    commands.registrar().register(cmd.build());
                }
                this.getLogger().info("Msg commands registered");
            }
        });

        // TODO: remove
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            NotCommand cmd = new NotCommand("devtest");
            cmd.onExecute(ctx -> {
                ctx.getSource().getSender().sendMessage("devtest");
                return 1;
            });

            NotStringArg type = new NotStringArg("type");
            type.setSuggestions(List.of("block", "item", "mob", "biome"));
            type.onExecute(ctx -> {
                String t = type.get(ctx);

                List<String> stuff = List.of();
                if (t.equals("block")) stuff = MinecraftStuff.getInstance().blockIdNames;
                if (t.equals("item")) stuff = MinecraftStuff.getInstance().itemIdNames;
                if (t.equals("mob")) stuff = MinecraftStuff.getInstance().entityIdNames;
                if (t.equals("biome")) stuff = MinecraftStuff.getInstance().biomeIdNames;

                for (String s : stuff) {
                    ctx.getSource().getSender().sendMessage(s);
                }

                return 1;
            });
            cmd.addArg(type);

            commands.registrar().register(cmd.build());
        });

        this.getLogger().info("Enabled successfully - Version: " + this.getPluginMeta().getVersion());
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

    public static Boolean hasPAPI() { return PlaceholderAPI; }

    public static HashMap<String, String> getChatFormats() {
        return chat_formats;
    }

    public static Permission getPerms() {
        return perms;
    }

}