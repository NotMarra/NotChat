package com.notmarra.notchat.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import static com.notmarra.notchat.NotChat.hasPAPI;

public class ChatFormatter {
    public static String K_PLAYER = "player";
    public static String K_TARGET = "target";
    public static String K_MESSAGE = "message";

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    private final Component baseComponent;
    private final List<Component> components = new ArrayList<>();
    private final HashMap<String, Object> replacements = new HashMap<>();

    public ChatFormatter(Component baseComponent) {
        this.baseComponent = baseComponent;
    }

    public String buildString() {
        return miniMessage.serialize(build());
    }

    public Component build() {
        Component baseComponent = this.baseComponent;

        for (Component component : components) {
            baseComponent = baseComponent.append(component);
        }

        for (String key : replacements.keySet()) {
            Object value = replacements.get(key);
            String valueString;

            if (value instanceof Component component) {
                valueString = miniMessage.serialize(component);
            } else {
                valueString = value.toString();
            }

            TextReplacementConfig replacementConfig = TextReplacementConfig.builder()
                .match("%" + key + "%")
                .replacement(valueString)
                .build();
            
            baseComponent = baseComponent.replaceText(replacementConfig);
        }

        if (hasPAPI()) {
            Player p;
            if (replacements.containsKey(K_PLAYER)) {
                p = (Player) replacements.get(K_PLAYER);
            } else {
                p = null;
            }
            String string = PlaceholderAPI.setPlaceholders(p, miniMessage.serialize(baseComponent));
            baseComponent = miniMessage.deserialize(string);
        }

        return baseComponent;
    }

    public ChatFormatter appendMany(ChatFormatter... formatters) {
        for (ChatFormatter formatter : formatters) {
            this.components.add(formatter.build());
        }
        return this;
    }

    public ChatFormatter appendMany(Component... components) {
        Collections.addAll(this.components, components);
        return this;
    }

    public ChatFormatter appendMany(String... strings) {
        for (String string : strings) {
            this.components.add(toComponent(string));
        }
        return this;
    }

    public ChatFormatter append(ChatFormatter formatter) {
        this.components.add(formatter.build());
        return this;
    }

    public ChatFormatter append(Component component) {
        this.components.add(component);
        return this;
    }

    public ChatFormatter append(String string) {
        this.components.add(toComponent(string));
        return this;
    }

    public ChatFormatter replace(String key, Object value) {
        this.replacements.put(key, value);
        return this;
    }

    public static ChatFormatter empty() {
        return new ChatFormatter(Component.empty());
    }

    public static ChatFormatter of(Component inputComponent) {
        return new ChatFormatter(inputComponent);
    }

    public static ChatFormatter of(String inputString) {
        return new ChatFormatter(toComponent(inputString));
    }

    public static ChatFormatter of(ChatFormatter otherFormatter) {
        return new ChatFormatter(otherFormatter.build());
    }

    public static Component toComponent(String inputString) {
        return miniMessage.deserialize(inputString);
    }
}
