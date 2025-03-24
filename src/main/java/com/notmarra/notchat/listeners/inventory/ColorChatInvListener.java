package com.notmarra.notchat.listeners.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.notmarra.notchat.NotChat;
import com.notmarra.notchat.listeners.BaseNotListener;
import com.notmarra.notchat.listeners.inventory.ColorChatInvHolder.ChatColor;
import com.notmarra.notlib.utils.ChatF;
import com.notmarra.notlib.utils.command.NotCommand;

import net.kyori.adventure.text.Component;

public class ColorChatInvListener extends BaseNotListener {
    public static final String ID = "color";

    private final Map<UUID, ChatColor> playerColors = new HashMap<>();

    // Keep track of which inventory belongs to which player
    private final Map<UUID, ColorChatInvHolder> openInventories = new HashMap<>();

    public static final String PERMISSION_COLOR = "notchat.color";
    public static final String PERMISSION_RELOAD = "notchat.color.reload";

    // config
    // TODO: this
    private String defaultColor;
    private boolean colorNames;
    private List<String> allowedColors;
    private String usePermission;
    private String specificColorPermission;

    public ColorChatInvListener(NotChat plugin) {
        super(plugin);
        
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean hasConfig() {
        return true;
    }

    @Override
    public void loadConfig() {
        defaultColor = config.getString("default_color", "");
        colorNames = config.getBoolean("color_names", false);
        allowedColors = config.getStringList("allowed_colors");
        usePermission = config.getString("permissions.use", PERMISSION_COLOR);
        specificColorPermission = config.getString("permissions.specific_color", PERMISSION_COLOR + ".%color%");
    }

    private void openColorSelection(Player player) {
        ColorChatInvHolder inventory = new ColorChatInvHolder(plugin);
        openInventories.put(player.getUniqueId(), inventory);
        player.openInventory(inventory.getInventory());
    }
    
    public boolean handleInventoryClick(Player player, ColorChatInvHolder holder, int slot) {
        UUID playerId = player.getUniqueId();
        
        Inventory clickedInventory = holder.getInventory();
        int size = clickedInventory.getSize();
        
        if (slot == size - 1) {
            playerColors.remove(playerId);
            ChatF.empty()
                .appendBold("Your chat color has been reset!", ChatF.C_GREEN)
                .sendTo(player);
            player.closeInventory();
            return true;
        }
        
        ItemStack clicked = clickedInventory.getItem(slot);
        
        if (clicked != null) {
            for (Map.Entry<ChatColor, Material> entry : holder.getColorMaterials().entrySet()) {
                if (clicked.getType() == entry.getValue()) {
                    ChatColor selectedColor = entry.getKey();
                    playerColors.put(playerId, selectedColor);
                    ChatF.empty()
                        .appendBold("Your chat color has been set to " + selectedColor.name() + "!", selectedColor.getColor())
                        .sendTo(player);
                    player.closeInventory();
                    return true;
                }
            }
        }
        
        return true;
    }
    
    public boolean handleInventoryClose(Player player) {
        UUID playerId = player.getUniqueId();
        return openInventories.remove(playerId) != null;
    }
    
    public Component applyColorToMessage(Player player, String message) {
        UUID playerId = player.getUniqueId();
        
        if (playerColors.containsKey(playerId)) {
            ChatColor color = playerColors.get(playerId);
            return ChatF.of(message, color.getColor()).build();
        }
        
        return ChatF.of(message).build();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        ColorChatInvHolder holder = openInventories.get(player.getUniqueId());

        if (holder != null && holder.getInventory().equals(clickedInventory)) {
            event.setCancelled(true);
            handleInventoryClick(player, holder, event.getSlot());
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory closedInventory = event.getInventory();
        ColorChatInvHolder holder = openInventories.get(player.getUniqueId());

        if (holder != null && holder.getInventory().equals(closedInventory)) {
            handleInventoryClose(player);
        }
    }

    @Override
    public List<NotCommand> getNotCommands() {
        return List.of(
            colorCommand()
        );
    }

    private NotCommand colorCommand() {
        NotCommand command =  NotCommand.of("color", cmd -> {
            openColorSelection(cmd.getPlayer());
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
}
