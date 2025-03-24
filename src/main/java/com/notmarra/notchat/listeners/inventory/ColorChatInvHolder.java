package com.notmarra.notchat.listeners.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import com.notmarra.notchat.NotChat;
import com.notmarra.notlib.utils.ChatF;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class ColorChatInvHolder implements InventoryHolder {
    private final NotChat plugin;
    private final Inventory inventory;
    private final Map<ChatColor, Material> colorMaterials = new HashMap<>();

    public ColorChatInvHolder(NotChat plugin) {
        this.plugin = plugin;
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
        this.inventory = prepareInventory();
    }

    private Inventory prepareInventory() {
        int rows = (int) Math.ceil(colorMaterials.size() / 9.0);
        ChatF title = ChatF.of("Select Chat Color", ChatF.C_BLACK);
        Inventory inventory = plugin.getServer().createInventory(this, rows * 9, title.build());
        
        ItemStack clearItem = new ItemStack(Material.BARRIER);
        ItemMeta clearMeta = clearItem.getItemMeta();
        clearMeta.displayName(ChatF.of("Clear Color", ChatF.C_RED).build());
        
        List<Component> clearLore = new ArrayList<>();
        clearLore.add(ChatF.of("Click to remove your chat color", ChatF.C_GRAY).build());
        clearMeta.lore(clearLore);
        
        clearItem.setItemMeta(clearMeta);
        inventory.setItem(rows * 9 - 1, clearItem);
        
        int i = 0;
        for (Map.Entry<ChatColor, Material> entry : colorMaterials.entrySet()) {
            ChatColor chatColor = entry.getKey();
            Material material = entry.getValue();
            
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            
            meta.displayName(ChatF.of(chatColor.name().replace('_', ' '), chatColor.getColor()).build());
            
            List<Component> lore = new ArrayList<>();
            lore.add(ChatF.of("Click to select this color", ChatF.C_GRAY).build());
            meta.lore(lore);
            
            item.setItemMeta(meta);
            inventory.setItem(i, item);
            i++;
        }

        return inventory;
    }

    public Map<ChatColor, Material> getColorMaterials() {
        return colorMaterials;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
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
