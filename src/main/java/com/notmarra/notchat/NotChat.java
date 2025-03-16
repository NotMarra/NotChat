package com.notmarra.notchat;

import com.notmarra.notchat.games.GameManager;
import com.notmarra.notchat.listeners.ChatListener;
import com.notmarra.notlib.utils.ChatF;
import com.notmarra.notchat.utils.Config;
import com.notmarra.notchat.utils.MinecraftStuff;
import com.notmarra.notlib.utils.command.NotCommand;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

import static com.notmarra.notchat.cmds.Chat.Msg;

public final class NotChat extends JavaPlugin {
    public static NotChat instance;
    public static Boolean Vault = false;
    public static Boolean PlaceholderAPI = false;
    public static ConfigurationSection chat_formats;
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
            chat_formats = Config.getConfigurationSection("chat_formats");
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
                CommandSender entity = ctx.getSource().getSender();

                ChatF message1 = ChatF.of("Hello, %player_name%!")
                    .withPlayer((Player) entity);
                entity.sendMessage(message1.build());

                ChatF message2 = ChatF.of("Your position is: %player_x%, %player_y%, %player_z%")
                    .withPlayer((Player) entity);
                entity.sendMessage(message2.build());

                ChatF message3 = ChatF.of("Target position is: %target_x%, %target_y%, %target_z%")
                    .withTargetPlayer((Player) entity);
                entity.sendMessage(message3.build());

                return 1;
            });

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

    public static ConfigurationSection getChatFormats() {
        return chat_formats;
    }

    public static Permission getPerms() {
        return perms;
    }

}