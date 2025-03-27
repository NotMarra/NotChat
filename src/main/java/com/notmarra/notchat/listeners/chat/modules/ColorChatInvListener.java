package com.notmarra.notchat.listeners.chat.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.notmarra.notchat.NotChat;
import com.notmarra.notchat.listeners.NotChatListener;
import com.notmarra.notlib.utils.ChatF;
import com.notmarra.notlib.utils.command.NotCommand;
import com.notmarra.notlib.utils.gui.NotGUI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class ColorChatInvListener extends NotChatListener {
    public static final String ID = "color";

    private final Map<UUID, ChatColor> playerColors = new HashMap<>();

    public static final String PERMISSION_COLOR = "notchat.color";
    public static final String PERMISSION_RELOAD = "notchat.color.reload";

    private final Map<ChatColor, Material> colorMaterials = new HashMap<>();

    // config
    // TODO: this
    @SuppressWarnings("unused")
    private String defaultColor;
    @SuppressWarnings("unused")
    private boolean colorNames;
    @SuppressWarnings("unused")
    private List<String> allowedColors;
    @SuppressWarnings("unused")
    private String usePermission;
    @SuppressWarnings("unused")
    private String specificColorPermission;

    public ColorChatInvListener(NotChat plugin) {
        super(plugin);
        colorMaterials.put(ChatColor.BLACK, Material.BLACK_WOOL);
        colorMaterials.put(ChatColor.DARK_BLUE, Material.BLUE_WOOL);
        colorMaterials.put(ChatColor.DARK_GREEN, Material.GREEN_WOOL);
        colorMaterials.put(ChatColor.DARK_AQUA, Material.CYAN_WOOL);
        colorMaterials.put(ChatColor.DARK_RED, Material.RED_WOOL);
        colorMaterials.put(ChatColor.DARK_PURPLE, Material.PURPLE_WOOL);
        colorMaterials.put(ChatColor.GOLD, Material.ORANGE_WOOL);
        colorMaterials.put(ChatColor.GRAY, Material.LIGHT_GRAY_WOOL);
        colorMaterials.put(ChatColor.DARK_GRAY, Material.GRAY_WOOL);
        colorMaterials.put(ChatColor.BLUE, Material.LIGHT_BLUE_WOOL);
        colorMaterials.put(ChatColor.GREEN, Material.LIME_WOOL);
        colorMaterials.put(ChatColor.AQUA, Material.LIGHT_BLUE_WOOL);
        colorMaterials.put(ChatColor.RED, Material.RED_WOOL);
        colorMaterials.put(ChatColor.LIGHT_PURPLE, Material.MAGENTA_WOOL);
        colorMaterials.put(ChatColor.YELLOW, Material.YELLOW_WOOL);
        colorMaterials.put(ChatColor.WHITE, Material.WHITE_WOOL);
    }

    @Override
    public String getId() { return ID; }

    @Override
    public void loadConfig() {
        defaultColor = config.getString("default_color", "");
        colorNames = config.getBoolean("color_names", false);
        allowedColors = config.getStringList("allowed_colors");
        usePermission = config.getString("permissions.use", PERMISSION_COLOR);
        specificColorPermission = config.getString("permissions.specific_color", PERMISSION_COLOR + ".%color%");
    }
    
    public Component applyColorToMessage(Player player, String message) {
        UUID playerId = player.getUniqueId();
        
        if (playerColors.containsKey(playerId)) {
            ChatColor color = playerColors.get(playerId);
            return ChatF.of(message, color.getColor()).build();
        }
        
        return ChatF.of(message).build();
    }

    @Override
    public List<NotCommand> notCommands() {
        return List.of(
            colorCommand()
        );
    }

    private NotCommand colorCommand() {
        NotCommand command =  NotCommand.of("color", cmd -> {
            Player player = cmd.getPlayer();

            NotGUI gui = NotGUI.create(ChatF.of("Select Chat Color"));
            gui.rows((int) Math.ceil(colorMaterials.size() / 9.0));

            // clear
            gui.addButton(
                Material.BARRIER,
                ChatF.of("Clear Color", ChatF.C_RED),
                gui.totalSize() - 1,
                (event, container) -> {
                    playerColors.remove(player.getUniqueId());
                    ChatF.empty()
                        .appendBold("Your chat color has been reset!", ChatF.C_GREEN)
                        .sendTo(player);
                    player.closeInventory();
                }
            );

            int i = 0;
            for (Map.Entry<ChatColor, Material> entry: colorMaterials.entrySet()) {
                ChatColor chatColor = entry.getKey();
                Material material = entry.getValue();

                gui.addButton(
                    material,
                    ChatF.of(chatColor.name().replace('_', ' '), chatColor.getColor()),
                    i,
                    (event, container) -> {
                        playerColors.put(player.getUniqueId(), chatColor);
                        ChatF.empty()
                            .appendBold("Your chat color has been set to " + chatColor.name() + "!", chatColor.getColor())
                            .sendTo(player);
                        player.closeInventory();
                    }
                );

                i++;
            }

            gui.open(player);
        });

        command.literalArg("help", arg -> {
            ChatF.empty()
                .appendBold("[NotChat " + getId().toUpperCase() + "] Commands:")
                .nl()
                .append("/color - Opens the chat color selection")
                .nl()
                .append("/color reload - Reload the configuration")
                .nl()
                .append("/color clear - Reset your chat color")
                .sendTo(arg.getPlayer());
        });

        // TODO: this?
        // reload.setPermission(PERMISSION_RELOAD);
        command.literalArg("reload", arg -> {
            Player player = arg.getPlayer();

            if (player.hasPermission(PERMISSION_RELOAD)) {
                reloadConfig();

                ChatF.empty()
                    .appendBold(getId().toUpperCase() + " configuration reloaded!", ChatF.C_GREEN)
                    .sendTo(player);
            } else {
                ChatF.empty()
                    .appendBold("You don't have permission to use this command!", ChatF.C_RED)
                    .sendTo(player);
            }
        });

        command.literalArg("clear", arg -> {
            Player player = arg.getPlayer();

            if (player.hasPermission(PERMISSION_COLOR)) {
                playerColors.remove(player.getUniqueId());

                ChatF.empty()
                    .appendBold("Your chat color has been reset!", ChatF.C_GREEN)
                    .sendTo(player);
            } else {
                ChatF.empty()
                    .appendBold("You don't have permission to use this command!", ChatF.C_RED)
                    .sendTo(player);
            }
        });

        return command;
    }

    public enum ChatColor {
        BLACK(TextColor.color(0x000000)),
        DARK_BLUE(TextColor.color(0x0000AA)),
        DARK_GREEN(TextColor.color(0x00AA00)),
        DARK_AQUA(TextColor.color(0x00AAAA)),
        DARK_RED(TextColor.color(0xAA0000)),
        DARK_PURPLE(TextColor.color(0xAA00AA)),
        GOLD(TextColor.color(0xFFAA00)),
        GRAY(TextColor.color(0xAAAAAA)),
        DARK_GRAY(TextColor.color(0x555555)),
        BLUE(TextColor.color(0x5555FF)),
        GREEN(TextColor.color(0x55FF55)),
        AQUA(TextColor.color(0x55FFFF)),
        RED(TextColor.color(0xFF5555)),
        LIGHT_PURPLE(TextColor.color(0xFF55FF)),
        YELLOW(TextColor.color(0xFFFF55)),
        WHITE(TextColor.color(0xFFFFFF));

        private final TextColor color;

        ChatColor(TextColor color) {
            this.color = color;
        }

        public TextColor getColor() {
            return color;
        }
    }
}
