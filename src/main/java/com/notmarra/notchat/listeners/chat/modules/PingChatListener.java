package com.notmarra.notchat.listeners.chat.modules;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.notmarra.notchat.NotChat;
import com.notmarra.notchat.listeners.NotChatListener;
import com.notmarra.notlib.utils.ChatF;
import com.notmarra.notlib.utils.command.NotCommand;

import io.papermc.paper.event.player.AsyncChatEvent;

public class PingChatListener extends NotChatListener {
    public static final String ID = "ping";

    private boolean messageEnabled;
    private String messageText;
    private boolean soundEnabled;
    private String soundName;
    private double soundVolume;
    private double soundPitch;
    private List<String> commands;

    private final Pattern mentionPattern = Pattern.compile("@(\\w+)");
    public static final String PERMISSION_PING = "notchat.ping";
    public static final String PERMISSION_RECEIVE = "notchat.ping.receive";
    public static final String PERMISSION_RELOAD = "notchat.ping.reload";

    public PingChatListener(NotChat plugin) {
        super(plugin);
        registerConfigurable();
    }

    @Override
    public String getId() { return ID; }

    @Override
    public void onConfigReload(List<String> reloadedConfigs) {
        FileConfiguration config = getConfig(getModuleConfigPath());

        messageEnabled = config.getBoolean("message.enabled", true);
        messageText = config.getString("message.text", "You have been pinged!");
        soundEnabled = config.getBoolean("sound.enabled", true);
        soundName = config.getString("sound.name", "entity.experience_orb.pickup");
        soundVolume = config.getDouble("sound.volume", 1.0);
        soundPitch = config.getDouble("sound.pitch", 1.0);
        commands = config.getStringList("commands");
    }

    @Override
    public List<NotCommand> notCommands() {
        return List.of(
            pingCommand()
        );
    }

    @EventHandler
    public void onAsyncChatEvent(AsyncChatEvent event) {
        if (event.isCancelled()) return;
        
        processMentions(
            event.getPlayer(),
            ChatF.of(event.message())
        );
    }
    
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) return;
        
        String command = event.getMessage().split(" ")[0].toLowerCase();
        
        if (commands.contains(command)) {
            processMentions(
                event.getPlayer(),
                ChatF.of(event.getMessage())
            );
        }
    }
    
    private void processMentions(Player sender, ChatF message) {
        if (!sender.hasPermission(PERMISSION_PING)) return;
        
        Matcher matcher = mentionPattern.matcher(message.buildString());
        
        while (matcher.find()) {
            String username = matcher.group(1);
            Player mentionedPlayer = Bukkit.getPlayerExact(username);
            
            if (mentionedPlayer != null && mentionedPlayer.isOnline()) {
                if (mentionedPlayer.hasPermission(PERMISSION_RECEIVE)) {
                    pingPlayer(sender, mentionedPlayer);
                }
            }
        }
    }
    
    private void pingPlayer(Player sender, Player target) {
        if (messageEnabled) {
            ChatF.empty()
                .withEntity(sender)
                .append(messageText, ChatF.C_YELLOW)
                .sendTo(target);
        }
        
        if (soundEnabled) {
            NamespacedKey soundKey = NamespacedKey.minecraft(soundName);
            Sound sound = Registry.SOUNDS.get(soundKey);

            if (sound != null) {
                target.playSound(target.getLocation(), sound, (float) soundVolume, (float) soundPitch);
            } else {
                getLogger().warn(ChatF.of("Sound not found: " + soundName, ChatF.C_ORANGE).build());
                target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, (float) soundVolume, (float) soundPitch);
            }
        }
    }

    NotCommand pingCommand() {
        NotCommand command = NotCommand.of("ping", cmd -> {
            ChatF.empty()
                .appendBold("[NotChat " + getId().toUpperCase() + "] Commands:")
                .nl()
                .append("/ping reload - Reload the configuration")
                .sendTo(cmd.getPlayer());
        });

        command.literalArg("reload", arg -> {
            Player player = arg.getPlayer();

            if (player.hasPermission(PERMISSION_RELOAD)) {
                reloadWithFiles();

                ChatF.empty()
                    .appendBold(getId().toUpperCase() + " configuration reloaded!", ChatF.C_GREEN)
                    .sendTo(player);
            } else {
                ChatF.empty()
                    .appendBold("You don't have permission to use this command!", ChatF.C_RED)
                    .sendTo(player);
            }
        });

        return command;
    }
}
